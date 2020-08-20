INSERT INTO `user`(`id`, `username`, `password`, `email`, `full_name`) VALUES (1, 'admin', '$2y$12$ilRv0EYaLStHNs1QFTIyB.rqlPnrreDaVcyFC6c5mh2VBBWF1xXFG', 'admin@test.ro', 'Administator');

INSERT INTO `permission`(`id`, `code`, `group`, `created`, `modified`) VALUES (1, 'admin', 'admin', '2020-08-02 13:43:03', '2020-08-02 13:43:03');

INSERT INTO `user_permission`(`user_id`, `permission_id`) VALUES (1, 1);