package com.pro.newsapp.ui.base

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.pro.newsapp.R
import com.pro.newsapp.common.util.NavigationUtil.navigateSingleTopTo
import com.pro.newsapp.data.database.entity.Article
import com.pro.newsapp.ui.screens.ArticleScreen
import com.pro.newsapp.ui.screens.NewsScreenPaging
import com.pro.newsapp.ui.screens.SavedScreen
import com.pro.newsapp.ui.screens.SearchScreen
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8


@Composable
fun NewsNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen =
        bottomBarScreens.find { it.route == currentDestination?.route } ?: Route.TopNews

    Scaffold(
        topBar = {
            NewsTopBar {
                if (navController.currentBackStackEntry?.destination?.route == Route.NewsArticle.route
                    || navController.currentBackStackEntry?.destination?.route == Route.FilterNews.route
                    || navController.currentBackStackEntry?.destination?.route == Route.TopNews.route
                ) {
                    navController.popBackStack()
                } else {
                    navigateSingleTopTo(Route.TopNews.route, navController)
                }
            }
        },
        bottomBar = {
            NewsBottomNavigation(
                currentScreen = currentScreen
            ) {
                navigateSingleTopTo(it.route, navController)
            }
        }
    ) {
        NewsNavHost(
            modifier = Modifier.padding(it),
            navController = navController
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsTopBar(onBackClicked: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = { onBackClicked() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun NewsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.TopNews.route,
        modifier = modifier
    ) {
        composable(
            route = Route.TopNews.route
        ) {
            NewsScreenPaging { article ->
                navigateToArticleScreen(article, navController)
            }
        }
        composable(route = Route.SavedNews.route) {
            SavedScreen {
                navigateToArticleScreen(it, navController)
            }
        }
        composable(route = Route.SearchNews.route) {
            SearchScreen(
                backPressed = {
                    navigateSingleTopTo(Route.TopNews.route, navController)
                }
            ) {
                navigateToArticleScreen(it, navController)
            }
        }
        composable(
            route = Route.NewsArticle.route,
            arguments = listOf(navArgument("article") { type = NavType.StringType })
        ) {
            val articleJson = it.arguments?.getString("article")
            val gson = Gson()
            val article = gson.fromJson(articleJson, Article::class.java)
            ArticleScreen(
                article = article
            )
        }
    }
}


@Composable
fun NewsBottomNavigation(
    currentScreen: Route,
    onIconSelected: (Route) -> Unit
) {
    NavigationBar {
        bottomBarScreens.forEach { screen ->
            NavigationBarItem(
                selected = screen == currentScreen,
                label = {
                    Text(text = stringResource(id = screen.resourceId))
                },
                icon = {
                    Icon(painter = painterResource(id = screen.icon), null)
                },
                onClick = {
                    onIconSelected.invoke(screen)
                }
            )
        }
    }
}

private fun navigateToArticleScreen(
    it: Article,
    navController: NavHostController
) {
    val articleJsonString = Gson().toJson(it)
    val encodedArticle = URLEncoder.encode(articleJsonString, UTF_8.name())
    navController.navigate(Route.NewsArticle.routeWithoutArgs + "/${encodedArticle}") {
        launchSingleTop = true
    }
}