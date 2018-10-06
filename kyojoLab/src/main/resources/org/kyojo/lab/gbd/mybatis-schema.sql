DROP TABLE IF EXISTS article_revision;

CREATE TABLE article_revision (
	seq INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	text CLOB NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO article_revision(name,text) VALUES('admin','The future is coming on, the past is gone by.');
