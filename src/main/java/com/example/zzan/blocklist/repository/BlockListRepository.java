package com.example.zzan.blocklist.repository;

import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockListRepository extends JpaRepository<BlockList,Long> {

	List<BlockList> findAllByBlockListingUserOrderByCreatedAtDesc(User user);
	Optional<BlockList> findByBlockListedUserAndBlockListingUser(User blockListedUser, User blockListingUser);
}
