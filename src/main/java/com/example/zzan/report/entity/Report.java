package com.example.zzan.report.entity;

import com.example.zzan.report.dto.ReportRequestDto;
import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String reportKind;

    @Column(nullable = true)
    private String another;

    @ManyToOne
    @JoinColumn(name = "ReportedUser_Id", nullable = false)
    private User reportedUser;

    @ManyToOne
    @JoinColumn(name = "ReportingUser_Id", nullable = false)
    private User reportingUser;

    public Report(User reportedUser, ReportRequestDto reportRequestDto, User user) {
        this.reportedUser = reportedUser;
        this.reportKind = reportRequestDto.getReportKind();
        this.another = reportRequestDto.getAnother();
        this.reportingUser = user;
    }
}