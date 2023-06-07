package com.example.zzan.blocklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.user.entity.User;

public interface BlockListRepository extends JpaRepository<BlockList,Long> {

	List<BlockList> findAllByBlockListingUserOrderByCreatedAtDesc(User user);
}
