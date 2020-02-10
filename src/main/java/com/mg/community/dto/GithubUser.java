package com.mg.community.dto;

import lombok.Data;

@Data
public class GithubUser {
    private String login;
    private String id;
    private String name;
    private String bio;
    private String url;
    private String avatarUrl;
}
