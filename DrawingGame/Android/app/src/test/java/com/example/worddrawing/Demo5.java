package com.example.worddrawing;
import android.util.Log;

import com.example.worddrawing.Activity.GalleryActivity;
import com.example.worddrawing.Activity.Login.LoginActivity;
import com.example.worddrawing.Activity.Register.RegisterActivity;
import com.example.worddrawing.Activity.Setting.SettingActivity;
import com.example.worddrawing.Adapter.FriendAdapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class Demo5 {

    private String Test_String;
    private int Test_int;

    @Test
    public void correctUsername(){
        Test_String="Hyegeun";
        LoginActivity testobject = new LoginActivity();

        String result = testobject.TestingUser();

        assertThat(result, is(Test_String));

    }

    @Test
    public void wrongPassword(){
        //Intented error
        //Test_String ="12as";
        Test_String="as12s";
        LoginActivity test = new LoginActivity();
        String result = test.TestingPassword();

        assertThat(result,is(Test_String));
    }

    @Test
    public void correctAccessWithUsernmae(){
        Test_String = "ahmed";
        RegisterActivity test = new RegisterActivity();
        String result = test.registerUser();

        assertThat(result,is(Test_String));

    }

    @Test
    public void correctAccessWithPassword(){
        Test_String = "asd123";
        RegisterActivity test = new RegisterActivity();
        String result = test.registerPassword();

        assertThat(result,is(Test_String));
    }

    @Test
    public void changePassword(){
        //Intented
        //Test_String="aaaa";
        Test_String="changed";
        SettingActivity test = new SettingActivity();
        //test.changePassword();
        String result = test.chaningPasswordTest();
        //when(test.chaningPasswordTest().equals(Test_String)).thenReturn(true);

        assertThat(result,is(Test_String));
    }
    @Test
    public void CreateRoom(){
        Test_String = "asd";
        SettingActivity test = new SettingActivity();

    }
    @Test
    public void GalleryActivity(){
        GalleryActivity gallery = new GalleryActivity();


    }

}
