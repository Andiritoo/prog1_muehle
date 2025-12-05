package com.github.Andiritoo.prog1_muehle.user_interface;

public interface UserInputProvider {

    Integer getClickedPosition();

    void clearClickedPosition();

    Integer getSelectedPosition();

    void setSelectedPosition(Integer position);
}
