package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domain.UserRole
import com.example.ui.screens.*
import com.example.ui.viewmodels.SharedViewModel

object Routes {
    const val LOGIN = "login"
    const val PROFILE_SELECTION = "profile_selection"
    const val TEACHER_DASHBOARD = "teacher_dashboard"
    const val TEACHER_CLASSES = "teacher_classes"
    const val ATTENDANCE = "attendance"
    const val GRADES = "grades"
    const val PARENT_DASHBOARD = "parent_dashboard"
    const val SCHOOL_DASHBOARD = "school_dashboard"
    const val SUPER_ADMIN_DASHBOARD = "super_admin_dashboard"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    sharedViewModel: SharedViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (currentUser == null) {
            val unauthNavController = rememberNavController()
            NavHost(
                navController = unauthNavController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onNavigateToProfileSelection = { email, name ->
                            unauthNavController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                            unauthNavController.currentBackStackEntry?.savedStateHandle?.set("name", name)
                            unauthNavController.navigate(Routes.PROFILE_SELECTION)
                        },
                        onNavigateToDashboard = {
                            // Do nothing, UI will recompose because currentUser is updated
                        },
                        authViewModel = authViewModel
                    )
                }
                composable(Routes.PROFILE_SELECTION) {
                    val email = unauthNavController.previousBackStackEntry?.savedStateHandle?.get<String>("email") ?: ""
                    val name = unauthNavController.previousBackStackEntry?.savedStateHandle?.get<String>("name") ?: ""

                    ProfileSelectionScreen(
                        initialEmail = email,
                        initialName = name,
                        onClose = { unauthNavController.popBackStack() },
                        onProfileSelected = { roleStr, outEmail, outName, schoolName ->
                            val role = when (roleStr) {
                                "Professor" -> UserRole.TEACHER
                                "Responsável" -> UserRole.PARENT
                                "Escola" -> UserRole.ADMIN
                                else -> UserRole.STUDENT
                            }
                            authViewModel.login(
                                email = if (email.isBlank()) "user@test.com" else email,
                                role = role,
                                name = if (name.isBlank()) "Usuário" else name,
                                schoolName = schoolName,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    )
                }
            }
        } else {
            val authNavController = rememberNavController()
            val startDest = when (currentUser?.role) {
                UserRole.TEACHER -> Routes.TEACHER_DASHBOARD
                UserRole.PARENT -> Routes.PARENT_DASHBOARD
                UserRole.ADMIN -> Routes.SCHOOL_DASHBOARD
                UserRole.SUPER_ADMIN -> Routes.SUPER_ADMIN_DASHBOARD
                else -> Routes.LOGIN
            }

            NavHost(
                navController = authNavController,
                startDestination = startDest
            ) {
                composable(Routes.TEACHER_DASHBOARD) {
                    TeacherDashboardScreen(
                        onNavigateToClasses = { authNavController.navigate(Routes.TEACHER_CLASSES) },
                        onNavigateToAttendance = { authNavController.navigate(Routes.ATTENDANCE) },
                        onNavigateToGrades = { authNavController.navigate(Routes.GRADES) },
                        onLogout = { authViewModel.logout() }
                    )
                }
                composable(Routes.TEACHER_CLASSES) {
                    TeacherClassesScreen(
                        onNavigateBack = { authNavController.popBackStack() },
                        onOpenClass = { /* TODO */ }
                    )
                }
                composable(Routes.ATTENDANCE) {
                    AttendanceScreen(
                        onNavigateBack = { authNavController.popBackStack() }
                    )
                }
                composable(Routes.GRADES) {
                    GradesScreen(
                        onNavigateBack = { authNavController.popBackStack() }
                    )
                }
                composable(Routes.PARENT_DASHBOARD) {
                    ParentDashboardScreen(
                        onLogout = { authViewModel.logout() }
                    )
                }
                composable(Routes.SCHOOL_DASHBOARD) {
                    SchoolDashboardScreen(
                        onLogout = { authViewModel.logout() },
                        sharedViewModel = sharedViewModel
                    )
                }
                composable(Routes.SUPER_ADMIN_DASHBOARD) {
                    SuperAdminDashboardScreen(
                        onLogout = { authViewModel.logout() },
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        }
    }
}
