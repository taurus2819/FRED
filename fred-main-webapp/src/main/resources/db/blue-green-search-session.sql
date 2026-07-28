-- Shared search state used by Blue and Green FRED instances.
create table if not exists fr.search_session_state (
    search_state_id uuid primary key,
    http_session_id varchar(128) not null,
    query_string text,
    status varchar(20) not null,
    error_message varchar(2000),
    created_at timestamp not null,
    updated_at timestamp not null,
    expires_at timestamp not null,
    constraint search_session_state_status_ck
        check (status in ('STARTED', 'COMPLETED', 'FAILED'))
);

create index if not exists search_session_state_session_idx
    on fr.search_session_state (http_session_id);

create index if not exists search_session_state_expiry_idx
    on fr.search_session_state (expires_at);

create table if not exists fr.search_session_result (
    search_state_id uuid not null,
    result_type varchar(20) not null,
    result_id integer not null,
    primary key (search_state_id, result_type, result_id),
    constraint search_session_result_state_fk
        foreign key (search_state_id)
        references fr.search_session_state (search_state_id)
        on delete cascade,
    constraint search_session_result_type_ck
        check (result_type in ('FEATURE', 'SAMPLE'))
);

create index if not exists search_session_result_lookup_idx
    on fr.search_session_result (search_state_id, result_type, result_id);

-- Run periodically after deployment.
-- delete from fr.search_session_state where expires_at <= current_timestamp;
