package com.example.redisdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    private Integer id;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
