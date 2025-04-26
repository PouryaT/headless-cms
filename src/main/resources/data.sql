-- Insert Authorities
INSERT INTO authority (id, authority_name) VALUES (1, 'READ');
INSERT INTO authority (id, authority_name) VALUES (2, 'CREATE');
INSERT INTO authority (id, authority_name) VALUES (3, 'EDIT');
INSERT INTO authority (id, authority_name) VALUES (4, 'DELETE');
-- Insert Roles
INSERT INTO role (id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, role_name) VALUES (2, 'ROLE_ADMIN');
-- Link Roles and Authorities
INSERT INTO role_authorities (role_id, authorities_id) VALUES (1, 1);
INSERT INTO role_authorities (role_id, authorities_id) VALUES (1, 2);
INSERT INTO role_authorities (role_id, authorities_id) VALUES (2, 1);
INSERT INTO role_authorities (role_id, authorities_id) VALUES (2, 2);
INSERT INTO role_authorities (role_id, authorities_id) VALUES (2, 3);
INSERT INTO role_authorities (role_id, authorities_id) VALUES (2, 4);
-- Insert Users
INSERT INTO "user" (id, username, password) VALUES (1, 'admin', 'adminpass');
INSERT INTO "user" (id, username, password) VALUES (2, 'user1', 'user1pass');
-- Link Users and Roles
INSERT INTO user_roles (user_id, roles_id) VALUES (1, 2); -- admin -> ROLE_ADMIN
INSERT INTO user_roles (user_id, roles_id) VALUES (2, 1); -- user1 -> ROLE_USER
-- Insert Categories
INSERT INTO category (id, name) VALUES (1, 'Technology');
INSERT INTO category (id, name) VALUES (2, 'Art');
-- Insert Posts (with created_at, updated_at, status)
INSERT INTO post (id, title, author_id, category_id, created_at, updated_at, status)
VALUES (1, 'First Post', 1, 1, NOW(), NOW(), 'PUBLISHED');
INSERT INTO post (id, title, author_id, category_id, created_at, updated_at, status)
VALUES (2, 'Second Post', 2, 2, NOW(), NOW(), 'DRAFT');
-- Insert Media (with post_id)
INSERT INTO media (id, url, type, post_id)
VALUES (1, 'https://example.com/image1.jpg', 'IMAGE', 1);
INSERT INTO media (id, url, type, post_id)
VALUES (2, 'https://example.com/video1.mp4', 'VIDEO', 2);
-- Insert Comments
INSERT INTO comment (id, content, post_id) VALUES (1, 'Nice post!', 1);
INSERT INTO comment (id, content, post_id) VALUES (2, 'Very helpful, thanks!', 1);
INSERT INTO comment (id, content, post_id) VALUES (3, 'Awesome video!', 2);
-- Insert Tags
INSERT INTO tag (id, name) VALUES (1, 'Spring Boot');
INSERT INTO tag (id, name) VALUES (2, 'Java');
INSERT INTO tag (id, name) VALUES (3, 'Docker');
-- Link Posts and Tags
INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1); -- First Post -> Spring Boot
INSERT INTO post_tags (post_id, tag_id) VALUES (1, 2); -- First Post -> Java
INSERT INTO post_tags (post_id, tag_id) VALUES (2, 3); -- Second Post -> Docker
