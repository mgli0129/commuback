package com.mg.community.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @ClassName PriorityDTO
 * @Description 热门话题DTO
 * @Author MGLi
 * @Date 2019/12/16 13:56
 * @Version 1.0
 */

@Data
public class PriorityDTO implements Comparable{
    private String tag;
    private Long priority;

    @Override
    public int compareTo(@NotNull Object o) {
        return (int) (priority - ((PriorityDTO) o).getPriority());
    }
}
