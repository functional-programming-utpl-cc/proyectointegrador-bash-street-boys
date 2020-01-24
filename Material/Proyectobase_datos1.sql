drop table USUARIO;
CREATE TABLE USUARIO (
	from_user VARCHAR2(50),
	user_lang VARCHAR2(10),
    from_user_id_str NUMBER,
    user_followers_count NUMBER (38, 0) NOT NULL,
	user_friends_count NUMBER (38, 0) NOT NULL,
	profile_image_url VARCHAR2(500),
	user_location VARCHAR(200),
	CONSTRAINT pk_from_user_id_str PRIMARY KEY (from_user_id_str));
     
insert into USUARIO (from_user, user_lang, from_user_id_str, profile_image_url, user_followers_count, user_friends_count, user_location)
select distinct from_user, user_lang, from_user_id_str, profile_image_url, user_followers_count, user_friends_count, user_location
from AUXILAR2;
commit;

select from_user_id_str, count(*) from auxilar2 group by from_user_id_str;
  /*  

INSERT INTO Usuario( from_user_id_str, from_user ,user_lang, profile_image_url, user_followers_count,
                    user_friends_count, user_location)
SELECT DISTINCT from_user_id_str, from_user ,user_lang, profile_image_url, user_followers_count,
       user_friends_count, user_location FROM ODS_2_3_COPIA;
       */