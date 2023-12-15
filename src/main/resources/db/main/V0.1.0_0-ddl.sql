create table if not exists account (
    id         bigserial   primary key,
    user_name  varchar(64) not null
);
create unique index if not exists uqaccount$user_name on account(user_name);

create table if not exists team (
    id    bigserial   primary key,
    title varchar(64) not null
);
create unique index if not exists uqteam$title on team(title);

create table if not exists hero (
    id         bigserial primary key,
    account_id bigint not null references account(id),
    team_id    bigint not null references team(id)
);
create unique index if not exists uqhero$account$team on hero(account_id, team_id);

create table if not exists task (
    id       bigserial   not null,
    title    varchar(64) not null,
    details  varchar(10000) not null,
    due_date date        not null,
    cost     varchar(16) not null, -- ISO 8601 hours and/or minutes
    status   varchar(16) not null default 'Open', -- Open, Work, Done
    priority varchar(16) not null default 'Normal', -- Low, Normal, High
    spent_total varchar(16) not null default 'PT0H', -- ISO 8601 hours  and/or minutes
    team_id     bigint      not null references team(id),
    chief_id    bigint      not null references hero(id),
    hero_id     bigint      not null references hero(id),
    created_at  timestamptz not null default now()
) partition by hash(team_id); -- considering a case with a huge amount of tasks :)
alter sequence if exists task_id_seq increment by 50;
create unique index if not exists uqtask$team$title on task(team_id, title);

create table if not exists task_spent (
    task_id  bigint not null,
    hero_id  bigint not null references hero(id),
    spent    varchar(16) not null, -- ISO 8601 hours and/or minutes
    added_at timestamptz not null default now()
) partition by range(added_at);
create index if not exists ixtask_spent$task$added_at on task_spent(task_id, added_at);
create index if not exists ixtask_spent$hero$added_at on task_spent(hero_id, added_at);
