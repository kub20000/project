-- --------------------------------------------------------
-- 호스트:                          192.168.0.116
-- 서버 버전:                        8.4.5 - MySQL Community Server - GPL
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- project 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `project` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `project`;

-- 테이블 project.comments 구조 내보내기
CREATE TABLE IF NOT EXISTS `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `posts_id` bigint DEFAULT NULL,
  `comments_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `comments_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `posts_id` (`posts_id`),
  CONSTRAINT `FK_comments_posts` FOREIGN KEY (`posts_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 project.comments:~10 rows (대략적) 내보내기
INSERT INTO `comments` (`id`, `posts_id`, `comments_name`, `comments_content`, `created_at`) VALUES
	(11, 9, NULL, '몰라', '2025-09-15 07:01:44'),
	(12, 4, NULL, '아아아아', '2025-09-15 07:02:34'),
	(13, 4, NULL, '잠깐ㄴ', '2025-09-15 07:02:39'),
	(16, 4, NULL, '', '2025-09-16 00:15:39'),
	(17, 4, NULL, '아아', '2025-09-16 00:15:43'),
	(29, 33, NULL, '1234', '2025-09-19 00:04:30'),
	(30, 33, NULL, '1234', '2025-09-19 00:05:01'),
	(31, 46, NULL, 'asd', '2025-09-19 00:05:28'),
	(32, 62, NULL, 'qwe', '2025-09-24 07:00:26'),
	(33, 62, NULL, 'asds', '2025-09-24 07:00:34');

-- 테이블 project.courses 구조 내보내기
CREATE TABLE IF NOT EXISTS `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `courses_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `courses_category` enum('life','skill','recipe') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `video_url` text COLLATE utf8mb4_general_ci,
  `duration_sec` int DEFAULT NULL,
  `like_count` int NOT NULL DEFAULT '0',
  `total_sec` int DEFAULT NULL,
  `thumbnail_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 project.courses:~18 rows (대략적) 내보내기
INSERT INTO `courses` (`id`, `courses_name`, `courses_category`, `description`, `video_url`, `duration_sec`, `like_count`, `total_sec`, `thumbnail_url`) VALUES
	(1, '비건 라이프 스타일 101: 기본 개념 수정테ㅡ트', 'life', '비건의 삶 ㅅㅈㅅㅈㅅㅈ', '/uploads/videos/3e2d3820-0b57-4f72-95fc-0ecf7299a746_videoSample.mp4', 39, 17, 20, '/uploads/thumbnails/5e3737df-e16c-4098-85e1-ce587255e332_imageSample.jpg'),
	(2, '영양 기초와 식단 구성', 'life', '비건 식단', 'https://www.youtube.com/embed/ab23D8in7QE', 10, 4, 115, NULL),
	(3, '조리 스킬: 칼질/팬 컨트롤', 'skill', '조리 기술', 'https://www.youtube.com/embed/QWJzDOUpqZk', 50, 6, 880, NULL),
	(4, '레시피: 고단백 샐러드 & 볼', 'recipe', '샐러드', 'https://www.youtube.com/embed/GMm7oaCcUPQ', 80, 3, 266, NULL),
	(5, '레시피: 베이킹(글루텐프리)', 'recipe', '글루텐없는 베이킹', NULL, 77, 1, 360, NULL),
	(6, '비건 외식 문화', 'life', '비건이 외식 하는법', NULL, 100, 99, 456, NULL),
	(7, '식재로 보관 / 라벨 읽기', 'skill', '뒷면을 보세', NULL, 98, 12, 7894, NULL),
	(10, 'ㅌㅅㅌ', 'life', 'ㅌㅅㅌ', '/uploads/videos/9bfbd87d-f43d-45d3-92e7-51fa9fdee5de_videoSample.mp4', NULL, 0, NULL, '/uploads/thumbnails/a319a008-c814-417d-8189-5a3b55044c77_gray.jpg'),
	(12, '124', 'recipe', '1234', '/uploads/videos/c9b221fd-337f-4e0e-b868-e12bafdda44b_Here\'s What Happens To Your Brain And Body When You Go Vegan  The Human Body - Insider Tech (720p, h264).mp4', NULL, 0, NULL, '/uploads/thumbnails/e477b3d6-040c-452b-aeba-c7076b9dfd2a_imageSample.jpg'),
	(14, '동영상 측정', 'life', 'ㅇㅇ', '/uploads/videos/0ef31fe3-7d3c-492f-bc03-1954035799e8_videoSample.mp4', NULL, 0, NULL, '/uploads/thumbnails/7f73d649-bc72-4d28-b3da-5a0f4d4d880b_gray.jpg'),
	(15, '영상 길이 측정', 'life', 'ㅊㅈ', '/uploads/videos/63f9205f-8592-42a1-bccd-5a4eaeee17c5_videoSample.mp4', NULL, 0, NULL, '/uploads/thumbnails/9d410b88-65d5-417a-b756-01ceec1055ce_imageSample.jpg'),
	(16, '썸네일 테스트1', 'life', '', '/uploads/videos/e2eb3578-b546-492b-b605-c97a574c9da7_', NULL, 0, NULL, '/uploads/thumbnails/1630691d-18e6-4047-af12-69aa264da892_24308511-stamp-with-word-sample-inside-vector-illustration.jpg'),
	(17, '썸네일 테스트2', 'life', '2', '/uploads/videos/0bd7d66a-93e9-4a0c-8736-36c07983d609_', NULL, 0, NULL, '/uploads/thumbnails/f80a1622-b0d0-4ae5-8a26-392701d0b3fb_61360644-sample-rubber-stamp-over-a-white-background.jpg'),
	(18, '썸네일 테스트3', 'life', '3', '/uploads/videos/37661c5f-af0d-4b5b-9659-ee9db139245d_', NULL, 0, NULL, '/uploads/thumbnails/7c3701d8-64b8-4a1f-99b1-3c68b3b524cf_istockphoto-1973365581-612x612.jpg'),
	(19, '썸네일 테스트4', 'life', '4', '/uploads/videos/3bb71d45-f1ef-46ff-88db-3045b4c9594c_', NULL, 0, NULL, '/uploads/thumbnails/2470f724-7690-4842-a9b6-f2d89f16674f_stock-vector-vector-illustration-of-sample-red-grunge-stamp-isolated-in-transparent-background-png-2065712915.jpg'),
	(20, '영상 길이 측정', 'life', 'ㅇㅇ', '/uploads/videos/f3771bcf-881d-4447-8239-201b3d517a02_Here\'s What Happens To Your Brain And Body When You Go Vegan  The Human Body - Insider Tech (720p, h264).mp4', NULL, 0, 0, '/uploads/thumbnails/8bace4d1-f5c4-4cec-93a8-91c5ab0e1e92_24308511-stamp-with-word-sample-inside-vector-illustration.jpg'),
	(21, 'ㅌㅅㅌ2222', 'life', 'ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ', '/uploads/videos/87c16670-407f-4981-9c54-d0061ca21a99_videoSample.mp4', NULL, 0, 20, '/uploads/thumbnails/c5a2d597-f98d-41aa-910d-ed8024b97b2d_imageSample.jpg'),
	(22, 'ㅌㅅㅌ333333333', 'life', '3', '/uploads/videos/ed16300d-e47c-4df2-b690-ca130d5fdc89_Here\'s What Happens To Your Brain And Body When You Go Vegan  The Human Body - Insider Tech (720p, h264).mp4', NULL, 0, 0, '/uploads/thumbnails/26c615cb-cf03-483b-8c98-231af8457cac_imageSample.jpg'),
	(27, 'ㅌㅅㅌ', 'life', 'ㅇㅇ', '/uploads/videos/8319d91b-c965-4ab4-9a9f-e1a4b3111895_videoSample.mp4', NULL, 0, 20, '/uploads/thumbnails/e4e010e8-09e0-4be1-ac57-58bf0bd8e14d_24308511-stamp-with-word-sample-inside-vector-illustration.jpg'),
	(28, '퀴즈 연결', 'life', 'ㅇ', '/uploads/videos/df3c5925-944e-47e0-8514-3084a28d65dd_videoSample.mp4', NULL, 0, 20, '/uploads/thumbnails/31dd4f48-8a46-4eed-abcd-0db60b3111f0_imageSample.jpg'),
	(29, '마지막 ㅌㅅㅌ', 'life', 'ㅈㅉㅇㅇ', '/uploads/videos/9d137d02-0fb0-46e3-8849-ba606e37743b_videoSample.mp4', NULL, 0, 20, '/uploads/thumbnails/b549965d-7d28-4247-92db-7c8a82af635d_stock-vector-vector-illustration-of-sample-red-grunge-stamp-isolated-in-transparent-background-png-2065712915.jpg'),
	(30, 'ㅌㅅㅌ2323', 'life', '231313', '/uploads/videos/3e61c21e-5637-4480-994c-86e1b6ad65d8_videoSample.mp4', NULL, 0, 20, '/uploads/thumbnails/448b0344-4681-4346-893e-efdd36981dc5_imageSample.jpg');

-- 테이블 project.instagram 구조 내보내기
CREATE TABLE IF NOT EXISTS `instagram` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `instagram_url` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 project.instagram:~0 rows (대략적) 내보내기

-- 테이블 project.myfridge 구조 내보내기
CREATE TABLE IF NOT EXISTS `myfridge` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ingredients` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `recipe` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 project.myfridge:~24 rows (대략적) 내보내기
INSERT INTO `myfridge` (`id`, `ingredients`, `recipe`) VALUES
	(1, '마늘, 감자', '[마늘 감자구이]  \n1. 감자는 껍질을 깨끗이 씻고 한 입 크기로 깍둑썰기 한다.  \n2. 마늘은 껍질을 까고 얇게 슬라이스한다.  \n3. 감자와 마늘을 볼에 넣고 올리브 오일, 소금, 후추를 넣어 잘 버무린다.  \n4. 예열된 오븐에 감자와 마늘을 골고루 펼쳐 180도에서 25~30분 동안 구워 노릇해질 때까지 익힌다.  \n5. 다 익으면 꺼내어 뜨겁게 바로 즐긴다.'),
	(2, '김치, 브로콜리', '[김치 브로콜리 볶음]  \n1. 브로콜리를 작은 송이로 나누어 깨끗이 씻고, 끓는 물에 2분간 데친 후 찬물에 헹궈 물기를 제거한다.  \n2. 김치는 한 입 크기로 썰어 준비한다.  \n3. 팬에 식용유를 두르고 중불에서 다진 마늘 1작은술을 볶아 향을 낸다.  \n4. 김치를 넣고 2~3분간 볶아 매콤한 김치향이 올라오게 한다.  \n5. 데친 브로콜리를 넣고 함께 볶으며 소금과 후추로 간을 맞춘다.  \n6. 마지막으로 참기름 몇 방울을 넣어 고소함을 더한 뒤 불을 끄고 완성한다.'),
	(3, '오이, 김치', '[오이 김치 비빔국수]  \n1. 오이는 깨끗이 씻어 얇게 채 썬다.  \n2. 김치는 적당한 크기로 잘라 준비한다.  \n3. 삶은 소면 또는 국수를 찬물에 헹구어 물기를 뺀다.  \n4. 큰 볼에 국수, 오이, 김치를 넣고 고추장, 간장, 참기름, 약간의 설탕(선택)을 넣어 고루 섞는다.  \n5. 기호에 따라 통깨를 뿌려 마무리하고 바로 먹는다.  \n\n쉽고 건강하게 즐길 수 있는 비건 한 끼입니다!'),
	(4, '가지, 상추', '[가지 상추 비건 쌈]  \n1. 가지는 길게 얇게 썰어 소금 조금 뿌려 10분간 절인다.  \n2. 물기를 닦아내고 팬에 올리브유를 두른 후 중약불에서 가지를 노릇하게 구워낸다.  \n3. 상추는 깨끗이 씻어 한 입 크기로 찢는다.  \n4. 각 쌈잎 위에 구운 가지 한 조각과 기호에 따라 쌈장 또는 고추장을 살짝 올린다.  \n5. 쌈을 말아 한 입 크기로 먹기 좋게 준비한다.  \n6. 신선한 채소와 구운 가지의 조화로 건강하고 가벼운 비건 쌈 완성!'),
	(5, '가지, 상추', '[가지 상추 비건 샐러드] 신선하고 가벼운 가지 상추 샐러드\n\n1. 가지는 깨끗이 씻어 1cm 두께로 썬 후, 팬에 약간의 올리브유를 두르고 앞뒤로 노릇하게 구워 식힌다.  \n2. 상추는 한 입 크기로 먹기 좋게 손으로 찢거나 자른다.  \n3. 큰 볼에 구운 가지와 상추를 넣고 섞는다.  \n4. 올리브유, 레몬즙, 소금, 후추를 넣어 드레싱을 만든 후 재료와 고루 버무린다.  \n5. 원한다면 다진 마늘이나 허브(파슬리, 바질 등)를 넣어 풍미를 더한다.  \n6. 바로 먹거나 냉장고에 잠시 두어 차갑게 즐긴다.'),
	(6, '가지, 상추', '[가지 상추 쌈]  \n1. 가지는 1cm 두께로 썰어 올리브유를 두른 팬에 앞뒤로 노릇하게 구워준다.  \n2. 상추는 깨끗이 씻어 물기를 제거한다.  \n3. 구운 가지 위에 다진 마늘, 참기름, 간장, 깨소금을 섞은 양념장을 살짝 바른다.  \n4. 상추 위에 양념한 가지를 올리고 쌈처럼 말아 맛있게 먹는다.  \n5. 기호에 따라 고추를 얇게 썰어 쌈에 곁들이면 더욱 풍미가 살아난다.'),
	(7, '가지, 상추', '[가지 상추 샐러드]  \n1. 가지를 약 1cm 두께로 썰어 소금 약간을 뿌려 10분간 절인다.  \n2. 절인 가지를 키친타월로 물기를 닦고 올리브유를 두른 팬에 노릇하게 구워 식힌다.  \n3. 상추는 깨끗이 씻어 먹기 좋은 크기로 자른다.  \n4. 큰 볼에 구운 가지와 상추를 넣고 올리브유, 레몬즙, 소금, 후추로 간을 맞춘다.  \n5. 잘 섞은 후 접시에 담아 바로 서빙한다.'),
	(8, '가지, 상추', '[가지 상추 쌈 무침]  \n1. 가지는 1cm 두께로 썰어 소금 약간 뿌려 10분간 두었다가 물기 제거.  \n2. 팬에 식용유 약간 둘러 가지를 앞뒤로 노릇하게 구워 식힌다.  \n3. 상추는 깨끗이 씻어 적당한 크기로 찢어 준비한다.  \n4. 볼에 다진 마늘 1작은술, 간장 2큰술, 참기름 1큰술, 고춧가루 약간, 설탕 조금을 넣고 양념장을 만든다.  \n5. 구운 가지와 상추를 양념장에 버무려 접시에 담아낸다.  \n6. 원하면 통깨를 뿌려 마무리한다.  \n\n건강하고 간단한 비건 쌈 무침 완성!'),
	(9, '가지, 상추', '[가지 상추 비빔밥]\n\n1. 가지를 깨끗이 씻은 후, 먹기 좋은 크기로 깍둑썰기한다.  \n2. 팬에 올리브유를 두르고 가지를 넣어 중불에서 부드럽게 익히고 소금으로 간을 한다.  \n3. 상추는 깨끗이 씻어 물기를 제거한 뒤 한 입 크기로 자른다.  \n4. 따뜻한 밥 위에 익힌 가지와 상추를 올리고, 고추장 대신 된장이나 간장, 참기름으로 만든 양념장을 곁들인다.  \n5. 고명으로 참깨를 뿌려 마무리한다.  \n\n간단하게 만들 수 있고, 가지의 부드러움과 상추의 신선함이 어우러져 건강한 한 끼가 됩니다.'),
	(10, '가지, 상추', '[가지 상추 비건 쌈]  \n1. 가지를 먹기 좋은 크기로 썰어 소금물에 10분 정도 담가 쓴맛을 제거한다.  \n2. 팬에 올리브유를 두르고 중불에서 가지를 앞뒤로 노릇하게 구워 식힌다.  \n3. 상추는 깨끗이 씻어 물기를 제거한다.  \n4. 구운 가지를 상추에 올리고, 기호에 따라 다진 마늘, 고추장, 참기름 약간을 곁들여 쌈을 만든다.  \n5. 모든 재료를 함께 한입 크기로 쌈 싸서 맛있게 먹는다.'),
	(11, '가지, 상추', '[가지 상추 쌈]  \n1. 가지는 1cm 두께로 썰어 소금 약간 뿌린 후 10분간 두어 쓴맛을 제거한다.  \n2. 팬에 올리브유를 두르고 가지를 넣어 양면이 노릇노릇해질 때까지 구워낸다.  \n3. 상추는 깨끗이 씻어 물기를 제거하고 한 장씩 준비한다.  \n4. 구운 가지를 적당한 크기로 잘라 상추 위에 올린다.  \n5. 기호에 따라 다진 마늘, 고추, 참기름, 간장으로 만든 소스를 찍어 쌈을 싸서 맛있게 먹는다.'),
	(12, '가지, 상추', '[가지 상추 비건 쌈 샐러드]\n\n1. 가지는 1cm 두께로 썰어 올리브유를 살짝 바른 뒤, 팬에 중불로 앞뒤로 노릇하게 구워준다.  \n2. 상추는 깨끗이 씻어 먹기 좋은 크기로 찢어 준비한다.  \n3. 볼에 구운 가지와 상추를 넣고, 다진 마늘 0.5큰술, 간장 1큰술, 참기름 1큰술, 레몬즙 1큰술, 깨 약간을 넣어 드레싱을 만든다.  \n4. 드레싱을 샐러드에 골고루 버무려 접시에 담아낸다.  \n5. 원한다면, 구운 가지 위에 통깨나 잘게 썬 실파를 올려 마무리한다.  \n\n간단하면서도 가지의 부드러운 식감과 상추의 신선함이 잘 어우러진 비건 요리입니다!'),
	(13, '가지, 상추, 파스타', '[가지 상추 파스타 샐러드] 상큼하고 건강한 비건 파스타 샐러드\n\n1. 가지는 한 입 크기로 깍둑 썰어 소금 약간을 뿌리고 15분간 절여 쓴맛을 제거한 뒤 물로 헹구고 물기를 제거한다.  \n2. 팬에 올리브유를 두르고 중불에서 가지를 노릇하게 구워준다.  \n3. 끓는 물에 파스타를 넣고 포장지에 적힌 시간보다 1분 덜 삶은 후 찬물에 헹궈 물기를 뺀다.  \n4. 큰 볼에 구운 가지, 삶은 파스타, 깨끗이 씻은 상추를 적당한 크기로 찢어 넣는다.  \n5. 올리브유, 레몬즙, 다진 마늘, 소금, 후추를 섞어 드레싱을 만든 후 샐러드에 버무린다.  \n6. 기호에 따라 견과류나 허브(바질, 파슬리)를 올려 마무리한다.'),
	(14, '양배추, 아보카도', '[양배추 아보카도 샐러드]  \n1. 양배추를 깨끗이 씻어 얇게 채 썬다.  \n2. 아보카도는 껍질과 씨를 제거한 후 작게 깍둑썰기 한다.  \n3. 큰 볼에 양배추와 아보카도를 넣고 섞는다.  \n4. 레몬즙 1큰술, 올리브오일 1큰술, 소금과 후추 약간을 넣어 드레싱을 만든다.  \n5. 드레싱을 샐러드에 부어 고루 버무린다.  \n6. 원한다면 아몬드 슬라이스나 통깨를 뿌려 마무리한다.  \n\n건강하고 간단한 비건 샐러드 완성!'),
	(15, '양배추, 아보카도', '[양배추 아보카도 샐러드] 신선하고 영양 가득한 비건 샐러드\n\n1. 양배추는 깨끗이 씻어 얇게 채 썬다.  \n2. 아보카도는 껍질과 씨를 제거한 후 먹기 좋은 크기로 깍둑썰기 한다.  \n3. 큰 볼에 채 썬 양배추와 아보카도를 넣고 잘 섞는다.  \n4. 올리브유, 레몬즙, 소금, 후추를 넣어 드레싱을 만든 후 샐러드에 골고루 버무린다.  \n5. 원한다면 발사믹 식초나 다진 허브를 추가해 풍미를 더한다.  \n6. 바로 먹거나 냉장고에 잠시 두어 차갑게 즐긴다.'),
	(16, '양배추, 아보카도', '[양배추 아보카도 샐러드]\n\n1. 양배추는 깨끗이 씻어 얇게 채 썬다.  \n2. 아보카도는 껍질과 씨를 제거한 뒤 먹기 좋은 크기로 깍둑썰기 한다.  \n3. 큰 볼에 양배추와 아보카도를 넣고 잘 섞는다.  \n4. 레몬즙 1큰술, 올리브오일 1큰술, 소금과 후추 약간을 넣어 드레싱을 만든다.  \n5. 드레싱을 샐러드에 골고루 뿌리고 부드럽게 버무린다.  \n6. 기호에 따라 다진 견과류나 허브를 올려 장식하면 완성!'),
	(17, '양배추, 아보카도', '[양배추 아보카도 비건 샐러드]  \n1. 양배추를 깨끗이 씻어 얇게 채 썬다.  \n2. 아보카도는 껍질과 씨를 제거한 후 깍둑썰기 한다.  \n3. 큰 볼에 양배추와 아보카도를 넣고 잘 섞는다.  \n4. 올리브 오일, 레몬즙, 소금, 후추를 넣어 드레싱을 만들어 샐러드에 버무린다.  \n5. 원한다면 견과류나 깨를 뿌려 식감을 더해 마무리한다.'),
	(18, '고구마, 양배추', '[고구마 양배추 비건 스팀 샐러드]  \n1. 고구마는 껍질을 벗기고 한입 크기로 깍둑썰기 한다.  \n2. 양배추는 깨끗이 씻어 굵게 채 썬다.  \n3. 찜기에 고구마를 넣고 10분간 쪄 부드럽게 익힌다.  \n4. 고구마가 익으면 양배추를 넣고 5분 더 쪄 아삭한 식감을 살린다.  \n5. 찐 고구마와 양배추를 볼에 담고, 올리브유, 소금, 후추, 레몬즙으로 가볍게 버무린다.  \n6. 기호에 따라 다진 견과류나 신선한 허브를 올려 마무리한다.'),
	(19, '쌀, 올리브오일', '[올리브오일 볶음밥]\n\n1. 쌀을 깨끗이 씻고 물에 30분 정도 불린 후 물기를 빼세요.  \n2. 팬에 올리브오일을 두르고 중불로 예열하세요.  \n3. 불린 쌀을 팬에 넣고 올리브오일과 잘 섞으면서 5~7분간 볶아주세요.  \n4. 기호에 따라 소금이나 후추로 간을 맞추고, 원한다면 야채나 허브를 추가해도 좋아요.  \n5. 따뜻할 때 바로 접시에 담아 건강한 올리브오일 볶음밥을 즐기세요!'),
	(20, '양배추, 퀴노아', '[양배추 퀴노아 영양밥]  \n1. 퀴노아는 깨끗이 씻어 물 1.5컵과 함께 냄비에 넣고 끓인다.  \n2. 끓기 시작하면 약한 불로 줄여 뚜껑을 덮고 15분간 익힌다.  \n3. 양배추는 얇게 채썰어 준비한다.  \n4. 팬에 올리브유를 두르고 양배추를 넣어 중불에서 부드러워질 때까지 볶는다.  \n5. 익힌 퀴노아와 소금, 후추로 간을 한 후 양배추와 잘 섞어 마무리한다.  \n6. 원한다면 다진 파슬리나 레몬즙을 약간 뿌려 상큼하게 즐긴다.'),
	(21, '양배추, 쌀', '[양배추 쌀 볶음밥] 건강하고 간단한 비건 양배추 쌀볶음밥\n\n1. 쌀은 깨끗이 씻어 20분 정도 불린 후, 평소 밥 짓는 방법대로 밥을 짓는다.  \n2. 양배추는 깨끗이 씻어 얇게 채 썰어 준비한다.  \n3. 팬에 식용유(또는 야채 육수)를 두르고 중불에서 양배추를 볶아 부드럽게 만든다.  \n4. 밥을 넣고 함께 골고루 볶아준 뒤, 소금과 후추로 간을 맞춘다.  \n5. 원한다면 다진 마늘이나 간장 조금을 추가해 풍미를 더한다.  \n6. 완성된 볶음밥을 그릇에 담아 따뜻하게 즐긴다.'),
	(22, '감자, 케일', '[케일 감자 볶음]  \n1. 감자는 껍질을 벗기고 얇게 썬다.  \n2. 케일은 깨끗이 씻어 한 입 크기로 자른다.  \n3. 팬에 식물성 오일을 두르고 중불에서 감자를 볶아 부드러워질 때까지 익힌다.  \n4. 케일을 넣고 함께 3~4분 더 볶아 케일이 살짝 숨이 죽도록 한다.  \n5. 소금과 후추로 간을 맞추고 기호에 따라 마늘 가루나 레몬즙을 살짝 뿌려 마무리한다.'),
	(23, '토마토, 시금치', '[토마토 시금치 비건 파스타]\n\n1. 파스타를 끓는 소금물에 8-10분간 삶아 체에 밉니다.  \n2. 팬에 올리브유를 두르고 다진 마늘을 중불에서 향이 날 때까지 볶습니다.  \n3. 토마토를 깍둑썰기 해 팬에 넣고 5분간 부드러워질 때까지 볶습니다.  \n4. 시금치를 넣고 숨이 죽을 때까지 재빨리 볶아줍니다.  \n5. 삶은 파스타를 팬에 넣고 소금, 후추로 간을 맞춘 후 잘 섞어 마무리합니다.  \n6. 원하면 바질이나 레몬즙을 약간 뿌려 상큼하게 즐기세요.'),
	(24, '고구마, 양배추', '[고구마 양배추 스팀 샐러드]  \n1. 고구마는 껍질을 벗기고 한입 크기로 깍둑썰기 한다.  \n2. 양배추는 깨끗이 씻어 채 썰어 준비한다.  \n3. 찜기에 고구마를 넣고 약 10~15분간 말랑해질 때까지 쪄준다.  \n4. 고구마가 익으면 양배추를 넣고 2~3분 더 함께 쪄 아삭함을 살린다.  \n5. 볼에 찐 고구마와 양배추를 담고 올리브유, 레몬즙, 소금, 후추로 간을 한다.  \n6. 잘 섞어 그릇에 담아 따뜻하게 혹은 식혀서 바로 제공한다.');

-- 테이블 project.posts 구조 내보내기
CREATE TABLE IF NOT EXISTS `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT (now()),
  `category` enum('NOTICE','FREEBOARD') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `fixed` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `author_id` (`author`) USING BTREE,
  CONSTRAINT `FK_posts_users` FOREIGN KEY (`author`) REFERENCES `users` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 project.posts:~33 rows (대략적) 내보내기
INSERT INTO `posts` (`id`, `author`, `title`, `content`, `created_at`, `category`, `fixed`) VALUES
	(3, NULL, 'test', 'test1', '2025-09-24 03:41:39', 'FREEBOARD', 1),
	(4, '더미01', '테스트01', '안\r\n녕\r\n하\r\n세\r\n요\r\n구\r\n르\r\n트\r\n라\r\n이\r\n앵\r\n글\r\n라\r\n이\r\n더\r\n워\r\n\r\nㅇ\r\nㅇ\r\nㅇ\r\nㅇ\r\nㅇ\r\n', '2025-09-08 05:53:52', 'NOTICE', 1),
	(9, NULL, '시간테스트', '몇시일까요잉', '2025-09-09 02:48:18', 'FREEBOARD', 0),
	(11, NULL, ' 아', '이스', '2025-09-09 03:48:37', 'FREEBOARD', 0),
	(13, NULL, '지금은 오후 5시', '11분', '2025-09-10 08:00:13', 'FREEBOARD', 0),
	(15, NULL, 'asd', 'asd', '2025-09-12 05:10:19', 'FREEBOARD', 0),
	(16, NULL, '12', '3', '2025-09-12 05:10:23', 'FREEBOARD', 0),
	(17, NULL, 'fd', 'sdfsa', '2025-09-12 05:10:26', 'FREEBOARD', 0),
	(18, NULL, 'asdf', 'asdf', '2025-09-12 05:10:29', 'FREEBOARD', 0),
	(19, NULL, 'asdfas', 'asdfasf', '2025-09-12 05:10:32', 'FREEBOARD', 0),
	(20, NULL, 'asdfasdf', 'asdfaf', '2025-09-12 05:10:36', 'FREEBOARD', 0),
	(26, NULL, '페이지당 10개 ', '넘어갈까?', '2025-09-12 05:11:27', 'FREEBOARD', 0),
	(28, NULL, '9 15 2 29', '시간', '2025-09-15 05:29:08', 'FREEBOARD', 0),
	(29, NULL, '내용 테스트', '아\r\n아\r\n아\r\n아\r\n앙\r\nㅏ\r\n아\r\n아\r\nㅇ\r\nㅏ', '2025-09-15 05:29:36', 'NOTICE', 0),
	(32, NULL, 'ㅇㅇ', 'ㅇㅇㅇ', '2025-09-15 05:39:23', 'FREEBOARD', 0),
	(33, NULL, '12314', '2314124', '2025-09-15 05:39:39', 'FREEBOARD', 0),
	(36, NULL, '테스트', '1', '2025-09-16 02:38:21', 'FREEBOARD', 0),
	(37, NULL, '테스트', '2', '2025-09-16 02:38:25', 'FREEBOARD', 0),
	(38, NULL, '1', '23', '2025-09-16 02:38:40', 'FREEBOARD', 0),
	(39, NULL, '123', '42', '2025-09-16 02:38:43', 'FREEBOARD', 0),
	(40, NULL, '166126', '2346436', '2025-09-16 02:38:46', 'FREEBOARD', 0),
	(41, NULL, '1467567', '345', '2025-09-16 02:38:50', 'FREEBOARD', 0),
	(42, NULL, '1090-1244', '7891234', '2025-09-16 02:38:53', 'FREEBOARD', 0),
	(46, NULL, '테스트카테고리', '그레고리3', '2025-09-16 06:14:55', 'FREEBOARD', 0),
	(49, NULL, '고정 테스트', '얍', '2025-09-16 08:07:24', 'NOTICE', 1),
	(50, NULL, '자유고정', 'ㅈㅉㅇㅇ?', '2025-09-16 08:08:58', 'FREEBOARD', 1),
	(54, NULL, '테스트01', '테스트1', '2025-09-23 15:53:36', 'NOTICE', 0),
	(55, NULL, 'test03', 'test08', '2025-09-23 16:01:57', 'NOTICE', 0),
	(56, NULL, '테스트05', '테스트', '2025-09-23 16:04:09', 'NOTICE', 0),
	(59, NULL, '123', '23', '2025-09-24 02:28:01', 'FREEBOARD', 0),
	(60, NULL, '210315', '561616', '2025-09-24 02:28:17', 'NOTICE', 1),
	(62, NULL, '안', '녕', '2025-09-24 04:34:16', 'FREEBOARD', 0),
	(63, NULL, '', '', '2025-09-24 04:45:07', 'FREEBOARD', 0);

-- 테이블 project.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coursesId` bigint DEFAULT NULL,
  `quiz_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `quiz_question` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `quiz_answer` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `quiz_result` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `courses_id` (`coursesId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 project.quiz:~5 rows (대략적) 내보내기
INSERT INTO `quiz` (`id`, `coursesId`, `quiz_name`, `quiz_question`, `quiz_answer`, `quiz_result`) VALUES
	(1, 1, '\'비건 라이프 스타일 101: 기본 개념\'', '육류, 생선은 물론 우유, 달걀, 꿀 등 동물에게서 얻은 식품을 전혀 섭취하지 않는 완전 채식주의자를 무엇이라고 하나요?', '1', '비건'),
	(2, 1, '\'비건 라이프 스타일 101: 기본 개념\'', '다음 중 비건이 섭취하지 않는 식품은 무엇인가요?\r\n1. 과일 \r\n2. 채소 \r\n3. 꿀 \r\n4. 곡식', '2', '3'),
	(3, 1, '\'비건 라이프 스타일 101: 기본 개념\'', '비건은 육류와 생선뿐만 아니라 우유, 달걀, 꿀 등 모든 동물성 식품을 거부합니다. (O/X)', '3', 'O'),
	(4, 1, '\'비건 라이프 스타일 101: 기본 개념\'', '비건은 강제로 해야하는 필수적인 것이다(O/X)', '4', 'X'),
	(5, 1, '\'비건 라이프 스타일 101: 기본 개념\'', '계란과 채소만 먹는 채식주의자 유형은?', '5', '오보 베지테리언'),
	(6, 28, 'Quiz 1', '1번 테스트', NULL, '1ㅂㄴ'),
	(7, 28, 'Quiz 2', '2번 테스트', NULL, '2번'),
	(8, 28, 'Quiz 3', '3 ㅌㅅㅌ', NULL, '3'),
	(9, 28, 'Quiz 4', '4', NULL, '4'),
	(10, 28, 'Quiz 5', '5', NULL, '5'),
	(11, 28, 'Quiz 1', '1번 테스트', NULL, '1ㅂㄴ'),
	(12, 28, 'Quiz 2', '2번 테스트', NULL, '2번'),
	(13, 28, 'Quiz 3', '3 ㅌㅅㅌ', NULL, '3'),
	(14, 28, 'Quiz 4', '4', NULL, '4'),
	(15, 28, 'Quiz 5', '5', NULL, '5'),
	(16, 29, 'Quiz 1', '1', '2', '1'),
	(17, 29, 'Quiz 2', '2', '3', '2'),
	(18, 29, 'Quiz 3', '3', '4', '3'),
	(19, 29, 'Quiz 4', '4', '5', '4'),
	(20, 29, 'Quiz 5', '5', '6', '5');

-- 테이블 project.users 구조 내보내기
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickname` varchar(60) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('ADMIN','USER','instructor') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER',
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `nickname` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 project.users:~5 rows (대략적) 내보내기
INSERT INTO `users` (`id`, `name`, `username`, `password`, `nickname`, `birthdate`, `email`, `phone`, `role`, `created_at`) VALUES
	(1, '', 'user01', '123', '더미01', NULL, 'kfjdkf@naver.com', NULL, 'ADMIN', '2025-09-07 09:43:12'),
	(2, '김영', 'user03', '1234', '강사', '2025-09-19', 'instr@naver.com', '01012345678', 'instructor', '2025-09-19 07:04:46'),
	(32, '박지성', 'user05', 'xo851729!2', 'skdlfg', '2025-09-02', 'skdkf@naver.com', '0417588888', 'USER', NULL),
	(33, '김유빈', 'a123', 'rladbqls12', 'qwe', '2025-09-12', '123@naver.com234', '01057605769', 'USER', NULL),
	(34, '철수', 'user08', 'xo3106', '더미03', '2025-09-02', 'andn@naver.com', '01022200000', 'USER', '2025-09-25 06:28:30');

-- 테이블 project.userscourses 구조 내보내기
CREATE TABLE IF NOT EXISTS `userscourses` (
  `id` bigint NOT NULL,
  `courses_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `courses_id` (`courses_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 project.userscourses:~0 rows (대략적) 내보내기

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
