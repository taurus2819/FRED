package nz.cri.gns.fred.servlet;

import com.google.common.base.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.FREDIPSysJspPage;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.util.StageUtil;

/**
 * Acts as a front controller for simple queries.
 *
 * On simple query submissions (post) this servlet will build the query and pass the
 * request off to ResultList.
 */
public class SimpleQueryServlet extends FREDHibernateServlet {

    private static final Logger log = Logger.getLogger(SimpleQueryServlet.class.getCanonicalName());

    private final ThreadLocal<StageUtil> stageUtil = new ThreadLocal<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/simple_query.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        // we stash the stage util in a thread local so we don't need to pass it
        // around all the time
        stageUtil.set(new StageUtil(factory));

        // we need to build the query an then defer to result list
        String tableName = "Sample AS s";
        List<String> tableJoins = new ArrayList<>();
        List<String> queryStrings = new ArrayList<>();
        List<String> constraints = new ArrayList<>();
        constraints.add("s.audit.status = 'approved'");
        constraints.add("s.feature.audit.status = 'approved'");

        try {
            // start by checking the three fields that are allowed for anyone.
            param(request, "Map").ifPresent(map -> {
                queryStrings.add("NZMG Sheet = " + map);
                constraints.add(String.format("s.feature.frNumber.mapSheet = '%s'", map.toUpperCase()));
            });
            param(request, "QMap").ifPresent(qmap -> {
                queryStrings.add(String.format("QMAP Sheet LIKE '%s'", qmap));
                String siteQuery = String.format("SITE_API.QMAP_SHEET = '%s'", qmap);
                constraints.add(siteQuery);
                request.setAttribute("SiteApiString", siteQuery);
            });
            param(request, "FieldNum").ifPresent(fieldNum -> {
                constraints.add("UPPER(s.feature.featureName) LIKE '%" + fieldNum.toUpperCase() + "%'");
                queryStrings.add("Field Number = " + fieldNum);
            });

            User loggedIn = FREDIPSysJspPage.getUser(request.getSession());
            if (loggedIn != null) {
                AtomicBoolean palListFlag = new AtomicBoolean(false);
                param(request, "Coll").ifPresent(coll -> {
                    constraints.add("UPPER(person.name) LIKE '%" + coll.toUpperCase() + "%'");
                    queryStrings.add("Collector = " + coll);
                    tableJoins.add(" JOIN s.collectors AS person");
                });
                paramAsInt(request, "YearFrom").ifPresent(yearFrom -> {
                    paramAsInt(request, "YearTo").ifPresentOrElse(yearTo -> {
                        Number[] years = toSortedRange(yearFrom, yearTo);
                        constraints.add("s.collectionDate BETWEEN '01-JAN-" + years[0] + "' AND '31-DEC-" + years[1] + "'");
                        queryStrings.add("Collection Date BETWEEN " + years[0] + " AND " + years[1]);
                    }, () -> {
                        constraints.add("s.collectionDate BETWEEN '01-JAN-" + yearFrom + "' AND '31-DEC-" + yearFrom + "'");
                        queryStrings.add("Collection Date = " + yearFrom);
                    });
                });

                param(request, "StratName").ifPresent(stratName -> {
                    constraints.add("UPPER(s.stratUnit) LIKE '%" + replaceSingleQuote(stratName.toUpperCase()) + "%'");
                    queryStrings.add("Stratigraphic Name = " + stratName);
                });
                param(request, "StratAtt").ifPresent(stratAtt -> {
                    // StratAtt is a checkbox, if it is present then it is on
                    constraints.add("(s.dip IS NOT NULL OR s.dipDirection IS NOT NULL OR s.strike IS NOT NULL)");
                    queryStrings.add("Stratal Attitude present");
                });
                param(request, "RockNat").ifPresent(rockNat -> {
                    constraints.add("UPPER(s.rockNature) LIKE '%" + replaceSingleQuote(rockNat.toUpperCase()) + "%'");
                    queryStrings.add("Nature of Rock Unit = " + rockNat);
                });
                param(request, "DepEnv").ifPresent(depEnv -> {
                    constraints.add("UPPER(s.depositionEnv) LIKE '%" + replaceSingleQuote(depEnv.toUpperCase()) + "%'");
                    queryStrings.add("Deposition Environment = " + depEnv);
                });

                param(request, "TaxonomicGroup").ifPresent(taxonomicGroup -> {
                    constraints.add("pal.taxonomicGroup.name = '" + taxonomicGroup + "'");
                    queryStrings.add("Taxonomic Group = " + taxonomicGroup);
                    palListFlag.set(true);
                });

                param(request, "Taxon").ifPresent(taxon -> {
                    constraints.add("UPPER(pal.taxon.taxonomicName) LIKE '%" + replaceSingleQuote(taxon.toUpperCase()) + "%'");
                    queryStrings.add("Taxonomic Name = " + taxon);
                    palListFlag.set(true);
                });

                paramAsInt(request, "StageFrom")
                        .flatMap(this::getAge)
                        .ifPresent(s1 -> {
                            Age s2 = paramAsInt(request, "StageTo")
                                    .flatMap(this::getAge)
                                    .orElse(s1);
                            // we sort the user provide ages so they just need to think about the endpoints
                            // and not have to get the correct order
                            Age[] ages = toSortedRange(s2, s1);
                            Age stageFrom = ages[1];
                            Age stageTo = ages[0];
                            String aQuery = stageFrom.getName() + " to " + stageTo.getName();
                            if (stageFrom.equals(stageTo)) {
                                aQuery = stageFrom.getName();
                            }
                            constraints.add("(sampleStageView.baseAge >= " + stageTo.getTopAge() + " AND sampleStageView.topAge <= " + stageFrom.getBaseAge() + ") AND sampleStageView.type in ('inferred', 'known', 'adoption', 'paleontology')");
                            tableJoins.add(" JOIN s.sampleStageViews AS sampleStageView");
                            queryStrings.add("Age= " + aQuery);
                        });

                paramAsFloat(request, "AgeFrom").ifPresent(ageFrom -> {
                    Float ageTo = paramAsFloat(request, "AgeTo").orElse(ageFrom);
                    Number[] ages = toSortedRange(ageFrom, ageTo);
                    String aQuery = ages[0] + " to " + ages[1];
                    if (ageFrom.equals(ageTo)) {
                        aQuery = String.valueOf(ages[0]);
                    }
                    constraints.add("(sampleStageView.baseAge >= " + ages[0] + " AND sampleStageView.topAge <= " + ages[1] + ") AND sampleStageView.type in ('inferred', 'known', 'adoption', 'paleontology')");
                    tableJoins.add(" JOIN s.sampleStageViews AS sampleStageView");
                    queryStrings.add("Age= " + aQuery);
                });

                if (palListFlag.get()) {
                    constraints.add("record.audit.status = 'approved'");
                    tableJoins.add(" JOIN s.records AS record JOIN record.paleontology.listEntries AS pal");
                }
                
                Optional<Integer> squirrelNarrowAge = paramAsInt(request, "SquirrelNarrowAgeFrom");
                Optional<Integer> squirrelWideAge = paramAsInt(request, "SquirrelWideAgeFrom");
                if (squirrelNarrowAge.isPresent() || squirrelWideAge.isPresent()) {
                    tableJoins.add("JOIN s.squirrelAge as squirrelAge");
                }                
                squirrelNarrowAge.flatMap(this::getAge)
                        .ifPresent(a1 -> {
                            Age a2 = paramAsInt(request, "SquirrelNarrowAgeTo")
                                    .flatMap(this::getAge)
                                    .orElse(a1);
                            // we sort the user provide ages so they just need to think about the endpoints
                            // and not have to get the correct order
                            Age[] ages = toSortedRange(a2, a1);
                            Age ageFrom = ages[1];
                            Age ageTo = ages[0];
                            String aQuery = ageFrom.getName() + " to " + ageTo.getName();
                            if (ageFrom.equals(ageTo)) {
                                aQuery = ageFrom.getName();
                            }
                            constraints.add("(squirrelAge.narrowBaseAge >= " + ageTo.getTopAge() + " AND squirrelAge.narrowTopAge <= " + ageFrom.getBaseAge() + ")");
                            queryStrings.add("Consensus narrow age= " + aQuery);
                        });
                squirrelWideAge.flatMap(this::getAge)
                        .ifPresent(a1 -> {
                            Age a2 = paramAsInt(request, "SquirrelWideAgeTo")
                                    .flatMap(this::getAge)
                                    .orElse(a1);
                            // we sort the user provide ages so they just need to think about the endpoints
                            // and not have to get the correct order
                            Age[] ages = toSortedRange(a2, a1);
                            Age ageFrom = ages[1];
                            Age ageTo = ages[0];
                            String aQuery = ageFrom.getName() + " to " + ageTo.getName();
                            if (ageFrom.equals(ageTo)) {
                                aQuery = ageFrom.getName();
                            }
                            constraints.add("(squirrelAge.wideBaseAge >= " + ageTo.getTopAge() + " AND squirrelAge.wideTopAge <= " + ageFrom.getBaseAge() + ")");
                            queryStrings.add("Consensus wide age= " + aQuery);
                        });
            }
        } catch (NumberFormatException e) {
            // we map any NFE from paramAsInt|Float to a servlet exception here.
            throw new ServletException(e.getMessage());
        }

        tableName = tableName + tableJoins.stream().collect(Collectors.joining(" ", " ", ""));
        String whereSQL = constraints.stream().collect(Collectors.joining(" AND "));
        log.log(Level.INFO, "Generated query: {0} {1}", new Object[]{tableName, whereSQL});

        request.setAttribute("TableName", tableName);
        request.setAttribute("WhereSQL", whereSQL);
        request.setAttribute("QueryString", queryStrings.stream().collect(Collectors.joining(" AND ")));
        request.getRequestDispatcher("result_list.jsp").forward(request, response);
    }

    Optional<Age> getAge(int ageId) {
        try {
            return Optional.ofNullable(stageUtil.get().getAge(ageId));
        } catch (StorageAccessException e) {
            return Optional.empty();
        }
    }

    Optional<Integer> paramAsInt(HttpServletRequest req, String paramName) throws NumberFormatException {
        return paramAsNumber(req, paramName, value -> Integer.valueOf(value));
    }

    Optional<Float> paramAsFloat(HttpServletRequest req, String paramName) throws NumberFormatException {
        return paramAsNumber(req, paramName, value -> Float.valueOf(value));
    }

    <T extends Number> Optional<T> paramAsNumber(HttpServletRequest req, String paramName,
            Function<String, T> converter) throws NumberFormatException {
        return param(req, paramName).map(value -> {
            try {
                return converter.apply(value);
            } catch (NumberFormatException e) {
                // huh, why are we catching NFE just to rethrow it. Just to put in the message we
                // want. This excecption will be caught in calling code and converted to a
                // ServletException (which is checked so much harder to throw from here)
                throw new NumberFormatException("Malformed parameter " + paramName + ". Value: " + value);
            }
        });
    }

    Optional<String> param(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);
        if (null == value) {
            return Optional.empty();
        }
        // we trim whitespace and remove any pesky single quote characters as they would
        // interfer with HQL constraints
        value = replaceSingleQuote(value.trim());
        if (value.isBlank() || "-".equals(value)) {
            // - is used by the page to indicate not selected
            return Optional.empty();
        }
        return Optional.of(value);
    }

    private Age[] toSortedRange(Age a1, Age a2) {
        if (a1.compareTo(a2) < 0) {
            return new Age[]{a1, a2};
        }
        return new Age[]{a2, a1};
    }

    /**
     * @return array containing a1 and a2 where the first element is the smaller of the two
     */
    private <T extends Number> Number[] toSortedRange(T a1, T a2) {
        if (a1.doubleValue() < a2.doubleValue()) {
            return new Number[] {a1, a2};
        }
        return new Number[] {a2, a1};
    }

    private String replaceSingleQuote(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return value;
        }
        // single quotes are evil in an sql statement. So we prune them out of user
        // submitted values.
        return value.replaceAll("'", "");
    }

}
