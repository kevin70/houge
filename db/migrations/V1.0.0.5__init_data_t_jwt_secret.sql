-- 测试密钥
insert into t_jwt_secret(id, algorithm, secret_key, create_time, update_time)
 values('A0', 'HS512', '4c427b7964b988933b68d2a1644b16b919e3255f9548f502352e2c08f0685557bef33dbdbf6416341e4c71b345f455e04237b7c9a6bc5617bdb03b4deb78f37b', now(), now());