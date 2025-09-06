package com.example.ElSilencio.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/empleado/dashboard")
    public String empleadoDashboard() {
        return "empleado/dashboard";
    }

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard() {
        return "cliente/dashboard";
    }
}
