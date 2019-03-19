/* 1 */
insert into category (title, keycode, cat_type, disp_order)
values ('고객센터', 'cserviceroot', 'CATEGORY', 1);

insert into category (title, keycode, cat_type, disp_order)
values ('K 쇼핑', 'kshoppingroot', 'CATEGORY', 2);

insert into category (title, keycode, cat_type, disp_order)
values ('샤오미 미니샵', 'xaiomishop', 'CATEGORY', 3);

insert into category (title, synonyms, parent_id, cat_type, disp_order, speach, image_path, image_alt_text, description)
values ('리모컨 사용 방법', '리모컨', 1, 'ITEM', 0, '<speak>다음과 같이 시도해 보시기 바랍니다. <audio src="https://actions.o2o.kr/content/test1.mp3"></audio></speak>', 'https://actions.o2o.kr/content/test1.gif', '리모컨 설정 이미지', '1. [확인 ○] + [선호채널] or [확인 ○] + [조용히] (동시에 누름) → LED ON \n2. [채널+] or [채널-] key를 천천히 TV화면이 꺼질때까지 누른다. (LED가 깜박임)\n 3. TV 화면이 꺼지면 [확인 ○] 누름 → 완료(LED 3회 점멸하며 꺼짐)');

/* 5 */
insert into category (title, synonyms, parent_id, cat_type, disp_order, speach, description)
values ('인터넷 연결 방법', '인터넷', 1, 'CATEGORY', 0, '다음중 선택 바랍니다.', 'SkyLife-TV 인터넷 연결방법');

insert into category (title, synonyms, parent_id, cat_type, disp_order, speach, description)
values ('비밀번호 변경 방법', '비밀번호', 1, 'ITEM', 0, '다음과 같이 시도해 보시기 바랍니다.', '비밀번호가 아이들이나 청소년에게 알려 졌을 경우를 대비하여 부모님들이 원하실 때 임의로 비밀번호를 변경할 수 있습니다. * 비밀번호 변경 방법 - 안드로이드 UHD 수신기 : TV홈 → 설정 → 자녀안심 → 비밀번호 변경 - 일반 UHD 수신기 : 메뉴 → 설정 → 자녀안심설정 → 비밀번호 변경" + "- HD 수신기 : 메뉴 → 사용자 메뉴 → 기능설정 → 비밀번호 설정 ※ 기타 수신기의 경우 고객센터(1588-3002)를 통해 확인해주시기 바랍니다. 이 비밀번호는 전체 EPG에서 사용되기 때문에 꼭 기억해 주시기 바랍니다.');

insert into category (title, synonyms, parent_id, cat_type, disp_order, speach, description)
values ('유선 연결방법', '유선', 5, 'ITEM', 0, '다음과 같이 시도해 보시기 바랍니다.', 'SkyLife-TV 유선 연결방법');

insert into category (title, synonyms, parent_id, cat_type, disp_order, speach, description)
values ('무선 (바밀번호)연결방법', '무선', 5, 'ITEM', 0, '다음과 같이 시도해 보시기 바랍니다.', 'SkyLife-TV 무선인터넷 (비밀번호) 연결방법');

insert into category (title, synonyms, parent_id, cat_type, disp_order, speach, description)
values ('무선 (WPS) 연결방법', '더블유피에스', 5, 'ITEM', 0, '다음과 같이 시도해 보시기 바랍니다.', 'SkyLife-TV 무선인터넷 (WPS) 연결방법');

insert into detail(category_id, item_type, linkurl)
values (4, 'CUSTOMER_INFO', 'https://youtu.be/8okO6PB305g');

select *
from category;