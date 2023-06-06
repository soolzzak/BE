package com.example.zzan.blacklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.zzan.blacklist.dto.BlacklistDto;
import com.example.zzan.blacklist.entity.Blacklist;
import com.example.zzan.user.entity.User;

public interface BlacklistRepository extends JpaRepository<Blacklist,Long> {

	List<Blacklist> findAllByBlackListingUserOrderByCreatedAtDesc(User user);
}
