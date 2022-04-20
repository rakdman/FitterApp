package com.fittr.server.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.fittr.server.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.multipart.MultipartFile;

public interface UserRepo extends JpaRepository<Users, Integer>{
	

	Optional<Users> findByEmail(String email);

	@Query(value="SELECT * FROM USER ORDER BY TOTAL_COINS DESC", nativeQuery=true)
	List<Users> getLeaderBoard();


//    public default void saveImage(MultipartFile imageFile) throws Exception {
//            String folder="C:\\Data\\Fulda\\Winter_Semester2021\\WebMobileDevelopement\\WebAndMobileDevProject_WS_21_GitLab\\androidServer\\server\\userimages\\";
//            byte[] bytes= imageFile.getBytes();
//            Path path= Paths.get(folder+imageFile.getOriginalFilename() );
//            Files.write(path,bytes);
//    }

//	int deleteByUserId(userId);


}
