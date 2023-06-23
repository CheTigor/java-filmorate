package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FsStatus {

    private int id;
    @NotNull
    @NotBlank
    private String status;

    public FsStatus(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public FsStatus(String status) {
        this.status = status;
    }
}
