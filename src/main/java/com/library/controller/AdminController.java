package com.library.controller;

import com.library.model.user.User;
import com.library.service.UserService;
import com.library.service.RentalService;
import com.library.service.ReservationService;
import com.library.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EventService eventService;

    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<User> addRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().add(userService.createRoleIfNotFound(roleName));
        User updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<User> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        User updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/users/{userId}/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long userId) {
        userService.toggleUserStatus(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/process-overdue-rentals")
    public ResponseEntity<Void> processOverdueRentals() {
        rentalService.processOverdueRentals();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> stats = Map.of(
                "totalUsers", userService.getAllUsers(org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements(),
                "activeRentals", rentalService.getActiveRentals().size(),
                "overdueRentals", rentalService.getOverdueRentals().size(),
                "upcomingEvents", eventService.getUpcomingEvents().size(),
                "activeReservations", reservationService.getActiveReservations().size()
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/rental-trends")
    public ResponseEntity<List<Object[]>> getRentalTrends(@RequestParam int year) {
        return ResponseEntity.ok(rentalService.getRentalStatsByMonth(year));
    }

    @GetMapping("/reports/customers-summary")
    public ResponseEntity<Map<String, Object>> getCustomersSummary() {
        List<Object[]> topCustomers = rentalService.getRentalStatsByMonth(
                java.time.LocalDate.now().getYear());

        Map<String, Object> summary = Map.of(
                "topCustomers", topCustomers,
                "customersWithOverdueRentals", rentalService.getOverdueRentals().stream()
                        .map(rental -> rental.getCustomer())
                        .distinct()
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(summary);
    }

    @PostMapping("/maintenance/cleanup-reservations")
    public ResponseEntity<Void> cleanupOldReservations() {
        reservationService.cleanupOldReservations();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/maintenance/generate-test-data")
    public ResponseEntity<String> generateTestData() {
        // This would generate test data for development purposes
        // Implementation details would depend on specific requirements
        return ResponseEntity.ok("Test data generation not implemented in this example");
    }

    @PostMapping("/backup/export-database")
    public ResponseEntity<String> exportDatabase() {
        // This would export the database for backup purposes
        // Implementation details would depend on specific requirements
        return ResponseEntity.ok("Database export not implemented in this example");
    }

    @PostMapping("/backup/import-database")
    public ResponseEntity<String> importDatabase(@RequestParam String backupFile) {
        // This would import a database backup
        // Implementation details would depend on specific requirements
        return ResponseEntity.ok("Database import not implemented in this example");
    }
}