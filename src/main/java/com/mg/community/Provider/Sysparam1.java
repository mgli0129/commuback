package com.mg.community.Provider;

public class Sysparam1 {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sysparam.redis
     *
     * @mbg.generated Thu Feb 20 22:47:39 CST 2020
     */
    private String redis;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sysparam.redis
     *
     * @return the value of sysparam.redis
     *
     * @mbg.generated Thu Feb 20 22:47:39 CST 2020
     */
    public String getRedis() {
        return redis;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sysparam.redis
     *
     * @param redis the value for sysparam.redis
     *
     * @mbg.generated Thu Feb 20 22:47:39 CST 2020
     */
    public void setRedis(String redis) {
        this.redis = redis == null ? null : redis.trim();
    }
}