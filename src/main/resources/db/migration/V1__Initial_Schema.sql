CREATE TABLE address_book (
  id bigint auto_increment,
  first_name varchar(50) not null,
  last_name varchar(50) not null,
  company_name varchar(50),
  address varchar(255),
  phone_number varchar(50),
  email varchar(50),
  primary key (id)
);

INSERT INTO address_book VALUES (1,
	'Tina',
	'Shelton',
	'',
	'5053 James St',
	'(421)-472-2270',
	'tina.shelton98@example.com'
);
INSERT INTO address_book VALUES (2,
	'Lily',
	'May',
	'Google',
	'2843 Arther St',
	'(514)-867-8005',
	'lily.may23@example.com'
);
INSERT INTO address_book VALUES (3,
	'Ramon',
	'Pena',
	'Microsoft',
	'6171 Wycliff Ave',
	'(609)-316-9655',
	'ramon.pena58@example.com'
);
INSERT INTO address_book VALUES (4,
	'Jeffery',
	'Moore',
	'',
	'1559 Corn St',
	'(612)-666-1422',
	'jeffery.moore72@example.com'
);
INSERT INTO address_book VALUES (5,
	'Ray',
	'Rodriquez',
	'',
	'6856 Samaritan Dr',
	'(783)-928-6429',
	'ray.rodriquez11@example.com'
);
