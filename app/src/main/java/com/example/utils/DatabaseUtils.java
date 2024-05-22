package com.example.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.data.Achievement;
import com.example.data.AchievementType;
import com.example.data.LoginData;
import com.example.data.Route;
import com.example.data.User;
import com.example.rally.App;
import com.example.rally.LoadingView;
import com.example.rally.MainActivity;
import com.example.rally.R;
import com.example.rally.RegisterActivity;
import com.example.rally.SplashActivity;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseUtils {

    private final Context context;
    private LoadingView loadingView;

    public DatabaseUtils(Context context, LoadingView loadingView) {
        this.context = context;
        this.loadingView = loadingView;
    }

    private static final String VALUE_CREATED_AT = "createdAt";

    private static final String ROUTE_NAME = "Routes";
    private static final String ROUTE_VALUE_CREATOR = "creator";
    private static final String ROUTE_VALUE_MARKERS_LNG = "markers_lng";
    private static final String ROUTE_VALUE_MARKERS_LOG = "markers_log";
    private static final String ROUTE_VALUE_WARNINGS_LNG = "warnings_lng";
    private static final String ROUTE_VALUE_WARNING_LOG = "warnings_log";
    private static final String ROUTE_VALUE_NAME = "name";
    private static final String ACHIEVEMENT_NAME = "Achievements";
    private static final String ACHIEVEMENT_VALUE_DESCRIPTION = "description";
    private static final String ACHIEVEMENT_VALUE_TYPE = "achievement_type";
    private static final String ACHIEVEMENT_VALUE_TARGET = "target";
    private static final String ACHIEVEMENT_TYPE = "achievement_type";
    private static final String USER_NAME = "Users";
    private static final String USER_VALUE_NAME = "username";
    private static final String USER_VALUE_ID = "user_id";
    private static final String USER_VALUE_ROUTES_TRAVELED = "routes_traveled";

    public static final String USER_VALUE_MAX_SPEED = "max_speed";
    public static final String USER_VALUE_KILOMETERS_TRAVELED = "kilometers_traveled";
    public static final String USER_VALUE_TIME_WITH_MORE_THAN_120_KM_PER_HOUR = "time_with_more_than_120_km_per_hour";

    public void updateUser(User newUserData) {
        showProgressDialog();
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(USER_NAME);
        userQuery.whereEqualTo(USER_VALUE_ID, getCurrentUserId());
        userQuery.getFirstInBackground((currentUserData, e) -> {
            hideProgressDialog();
            if (e == null) {
                currentUserData.put(USER_VALUE_ROUTES_TRAVELED, newUserData.routesTraveled);
                currentUserData.put(USER_VALUE_MAX_SPEED, newUserData.maxSpeed);
                currentUserData.put(USER_VALUE_KILOMETERS_TRAVELED, newUserData.kilometersTraveled);
                currentUserData.put(USER_VALUE_TIME_WITH_MORE_THAN_120_KM_PER_HOUR, newUserData.timeWithMoreThan120KmPerHour);
                currentUserData.saveInBackground(errorWhileSaving -> {
                    if (errorWhileSaving == null) {
                        showSuccess();
                    } else {
                        showError(errorWhileSaving);
                    }
                });
            } else {
                showError(e);
            }
        });
    }

    public void getUserData(Consumer<User> onSuccess) {
        showProgressDialog();
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(USER_NAME);
        userQuery.whereEqualTo(USER_VALUE_ID, getCurrentUserId());
        userQuery.findInBackground((objects, e) -> {
            hideProgressDialog();
            if (e == null) {
                ParseObject user = objects.get(0);
                onSuccess.accept(new User(
                        user.getString(USER_VALUE_NAME),
                        user.getInt(USER_VALUE_ROUTES_TRAVELED),
                        user.getInt(USER_VALUE_MAX_SPEED),
                        user.getInt(USER_VALUE_KILOMETERS_TRAVELED),
                        user.getInt(USER_VALUE_TIME_WITH_MORE_THAN_120_KM_PER_HOUR)
                ));
            }
        });
    }

    public void addNewUser(String userName) {
        showProgressDialog();
        ParseObject newUser = new ParseObject(USER_NAME);
        newUser.put(USER_VALUE_NAME, userName);
        newUser.put(USER_VALUE_ID, getCurrentUserId());
        newUser.put(USER_VALUE_MAX_SPEED, 0);
        newUser.put(USER_VALUE_ROUTES_TRAVELED, 0);
        newUser.put(USER_VALUE_KILOMETERS_TRAVELED, 0);
        newUser.put(USER_VALUE_TIME_WITH_MORE_THAN_120_KM_PER_HOUR, 0);
        newUser.saveInBackground(e -> {
            if (e == null) showSuccess();
            else showError(e);
        });
    }

    public void getRoutes(Consumer<List<Route>> onSuccess) {
        showProgressDialog();
        ParseQuery<ParseObject> routesQuery = ParseQuery.getQuery(ROUTE_NAME);
        routesQuery.orderByAscending(VALUE_CREATED_AT);
        routesQuery.whereEqualTo(ROUTE_VALUE_CREATOR, getCurrentUserId());
        routesQuery.findInBackground((objects, e) -> {
            hideProgressDialog();
            if (e == null) {
                List<Route> routes = new ArrayList<>();
                objects.forEach(parseObject -> routes.add(
                        new Route(
                                parseObject.getString(ROUTE_VALUE_NAME),
                                (ArrayList<Double>) parseObject.get(ROUTE_VALUE_MARKERS_LNG),
                                (ArrayList<Double>) parseObject.get(ROUTE_VALUE_MARKERS_LOG),
                                (ArrayList<Double>) parseObject.get(ROUTE_VALUE_WARNINGS_LNG),
                                (ArrayList<Double>) parseObject.get(ROUTE_VALUE_WARNING_LOG)
                        )
                ));
                onSuccess.accept(routes);
            } else {
                showError(e);
            }
        });
    }

    public void addRoute(
            List<Double> markersLng,
            List<Double> markersLog,
            List<Double> warningsLng,
            List<Double> warningsLog,
            String routeName
    ) {
        showProgressDialog();
        ParseObject newRoute = new ParseObject(ROUTE_NAME);
        newRoute.put(ROUTE_VALUE_MARKERS_LNG, markersLng);
        newRoute.put(ROUTE_VALUE_MARKERS_LOG, markersLog);
        newRoute.put(ROUTE_VALUE_WARNING_LOG, warningsLog);
        newRoute.put(ROUTE_VALUE_WARNINGS_LNG, warningsLng);
        newRoute.put(ROUTE_VALUE_CREATOR, getCurrentUserId());
        newRoute.put(ROUTE_VALUE_NAME, routeName);
        newRoute.saveInBackground(error -> {
            hideProgressDialog();
            if (error == null) {
                showSuccess();
            } else {
                showError(error);
            }
        });
    }

    public void getAchievements(Consumer<List<Achievement>> onSuccess) {
        showProgressDialog();
        ParseQuery<ParseObject> achievementsQuery = ParseQuery.getQuery(ACHIEVEMENT_NAME);
        achievementsQuery.findInBackground(((objects, e) -> {
            hideProgressDialog();
            if (e == null) {
                List<Achievement> achievements = new ArrayList<>();
                objects.forEach(parseObject -> {
                    achievements.add(
                            new Achievement(
                                    (String) parseObject.get(ACHIEVEMENT_VALUE_DESCRIPTION),
                                    (int) parseObject.get(ACHIEVEMENT_VALUE_TARGET),
                                    AchievementType.valueOf(parseObject.getString(ACHIEVEMENT_VALUE_TYPE))
                            )
                    );
                });
                onSuccess.accept(achievements);
            } else {
                showError(e);
            }
        }));
    }

    public void loginUser(String userName, String password, Consumer<ParseUser> onSuccess) {
        showProgressDialog();
        ParseUser.logInInBackground(userName, password, (user, e) -> {
            hideProgressDialog();
            if (user != null) {
                onSuccess.accept(user);
            } else {
                ParseUser.logOut();
                showError(e);
            }
        });
    }

    public void registerUser(String username, String password, Consumer<Object> onSuccess) {
        showProgressDialog();
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(e -> {
            hideProgressDialog();
            if (e == null) {
                addNewUser(username);
                onSuccess.accept(new Object());
            } else {
                ParseUser.logOut();
                showError(e);
            }
        });
    }

    public void removeUser(Consumer<Object> onSuccess) {
        showProgressDialog();
        ParseQuery<ParseObject> userToDelete = ParseQuery.getQuery(USER_NAME);
        userToDelete.whereEqualTo(USER_VALUE_ID, getCurrentUserId());
        try {
            ParseUser.getCurrentUser().delete();
            userToDelete.getFirst().deleteInBackground(e -> {
                hideProgressDialog();
                if (e == null) {
                    onSuccess.accept(new Object());
                } else {
                    showError(e);
                }
            });
        } catch (Exception ex) {
            hideProgressDialog();
            ex.printStackTrace();
        }

    }

    private void showProgressDialog() {
        loadingView.show();
    }

    private void hideProgressDialog() {
        loadingView.hide();
    }

    private void showSuccess() {
        Toast.makeText(context, context.getResources().getString(R.string.data_updated), Toast.LENGTH_SHORT).show();
    }

    private void showError(ParseException parseException) {
        Toast.makeText(context, parseException.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private String getCurrentUserId() {
        return ParseUser.getCurrentUser().getObjectId();
    }
}
