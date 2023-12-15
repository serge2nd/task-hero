do 'declare start_date varchar; begin
  for i in 0..7 loop execute format(''
    create table if not exists task%s partition of task
    for values with (modulus 8, remainder %1$s);
    create unique index if not exists uqtask%1$s$id on task%1$s(id);
    '', i
  ); end loop;
  for start_date in values
    (''202312''), (''202401''), (''202402''), (''202403'')
  loop execute format(''
    create table if not exists task_spent%s partition of task_spent for values
    from (%2$L) to (%2$L::date + interval''''P1M'''' - interval''''P1D'''')
    '', start_date, to_date(start_date, ''YYYYMM'')
  ); end loop;
end';
