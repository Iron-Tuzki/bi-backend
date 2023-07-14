alter table chart
    add status enum ('wait', 'running', 'success', 'fail') default 'wait' not null comment 'AI执行状态' after userId;
alter table chart
    add execMessage text null comment 'AI执行信息' after status;