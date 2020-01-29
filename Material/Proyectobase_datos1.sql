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
drop table TWEET;
CREATE TABLE TWEET(
    text VARCHAR(280),
    created_at Date,
    creat_at DATE,
    time DATE,
    geo_cordinates NUMBER (38, 0)
    in_reply_to_user_id_str CHAR,
    in_reply_to_user_id_str CHAR);
// SE crea una tabla auxiliar donde se selecion solo los fron_user_id 
solo va a selecionar a los mas actuales, se hace un selec distyn, referenciando a la tabla donde estas ods totlmente limpio
luego se compara con la tabla ausiliar2 con la tabla original obs, donde la fecha sea igual a la fecha creada anterior cojera los que importan

);
/

   
    
    CREATE TABLE auxliar2  as
(
    SELECT from_user_id_str,MAX (to_date(substr(time, 1, 20), 'DD-MM-RR HH24:MI:SS'))  as fecha FROM AUXILIAR group by (FROM_USER_ID_STR)
);

select DISTINCT A.FROM_USER_ID_STR, A.from_user, A.profile_image_url, A.user_followers_count, A.user_friends_count, A.user_location
from AUXILIAR A, aux
     WHERE aux.FROM_USER_ID_STR = A.from_user_id_str
        and aux.fecha = to_date(substr(time, 1, 20), 'DD-MM-RR HH24:MI:SS') ;


CREATE TABLE USUARIO (
    FROM_USER_ID_STR INT,
    FROM_USER VARCHAR2(60),
    USER_LANG VARCHAR2(10),
    PROFILE_IMAGE_URL VARCHAR2(250),
    USER_FOLLOWERS_COUNT INT,
    USER_FRIENDS_COUNT INT,
    USER_LOCATION VARCHAR2(200),
    CONSTRAINT FROM_USER_ID_STR_PK PRIMARY KEY (FROM_USER_ID_STR)
);
 */
