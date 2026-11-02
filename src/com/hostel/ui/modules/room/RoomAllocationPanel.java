package com.hostel.ui.modules.room;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Room;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class RoomAllocationPanel extends JPanel {
    private final User currentUser;

    private StatCard statTotalRooms;
    private StatCard statOccupiedBeds;
    private StatCard statAvailableBeds;
    private StatCard statFullRooms;

    private CustomComboBox<String> blockFilter;
    private CustomComboBox<String> statusFilter;
    private CustomTextField searchField;

    private CustomTable table;
    private DefaultTableModel tableModel;
    private List<Room> currentRoomList;

    public RoomAllocationPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header Title & Action Buttons
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Key Stats Row
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(18));

        // Filters Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Rooms Table Card
        content.add(createTableCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.ROOM_ALLOCATED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.STUDENT_CREATED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.STUDENT_DELETED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("Bed Allocation & Capacity Manager");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Live block occupancy, bed allocations, room transfers and facility monitoring");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        CustomButton allocateBtn = new CustomButton("🛌 Allocate Bed", CustomButton.Variant.PRIMARY);
        allocateBtn.addActionListener(e -> openAllocateDialog());

        CustomButton transferBtn = new CustomButton("🔄 Transfer / Change Room", CustomButton.Variant.SECONDARY);
        transferBtn.addActionListener(e -> openTransferDialog());

        btnPanel.add(allocateBtn);
        btnPanel.add(transferBtn);

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statTotalRooms = new StatCard("Total Rooms", "0", "Across all blocks", UITheme.PRIMARY, "🏢");
        statOccupiedBeds = new StatCard("Occupied Beds", "0", "Assigned residents", UITheme.INFO, "🛌");
        statAvailableBeds = new StatCard("Available Beds", "0", "Ready to allocate", UITheme.SUCCESS, "✨");
        statFullRooms = new StatCard("Full Rooms", "0", "100% capacity", UITheme.DANGER, "🔒");

        row.add(statTotalRooms);
        row.add(statOccupiedBeds);
        row.add(statAvailableBeds);
        row.add(statFullRooms);
        return row;
    }

    private JPanel createFilterToolbar() {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        searchField = new CustomTextField("Search by room number or block...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyFilters();
            }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        blockFilter = new CustomComboBox<>(new String[]{"All Blocks", "A Block (North Wing)", "B Block (South Wing)", "C Block (Green Wing)"});
        statusFilter = new CustomComboBox<>(new String[]{"All Statuses", "AVAILABLE", "PARTIAL", "FULL", "MAINTENANCE"});

        blockFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        filters.add(new JLabel("Filter:"));
        filters.add(blockFilter);
        filters.add(statusFilter);

        card.add(searchField, BorderLayout.WEST);
        card.add(filters, BorderLayout.EAST);
        return card;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        String[] cols = {"Room No", "Block", "Floor", "Type", "Capacity", "Occupied", "Available Beds", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new CustomTable(tableModel);

        // Status Badge Renderer (Clear visual distinction: Green for Available, Amber for Partial, Red for Full)
        table.getColumnModel().getColumn(7).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "AVAILABLE";
            Color c = UITheme.SUCCESS;
            if ("PARTIAL".equalsIgnoreCase(val)) c = UITheme.WARNING;
            else if ("FULL".equalsIgnoreCase(val)) c = UITheme.DANGER;
            else if ("MAINTENANCE".equalsIgnoreCase(val)) c = UITheme.TEXT_MUTED;
            return new StatusBadge(val, c);
        });

        table.getColumnModel().getColumn(8).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            JButton btn = new JButton("View Occupants 👁");
            btn.setFont(UITheme.FONT_TINY);
            btn.setFocusPainted(false);
            p.add(btn);
            return p;
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && currentRoomList != null && row < currentRoomList.size()) {
                    Room r = currentRoomList.get(row);
                    if (col == 8 || e.getClickCount() == 2) {
                        openRoomDetailsDialog(r);
                    }
                }
            }
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 420));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Room> all = db.getAllRooms();

        int totalCap = all.stream().mapToInt(Room::getCapacity).sum();
        int totalOcc = all.stream().mapToInt(Room::getOccupied).sum();
        int totalAvail = Math.max(0, totalCap - totalOcc);
        long fullCount = all.stream().filter(r -> r.getStatus() == Room.RoomStatus.FULL).count();

        statTotalRooms.setValue(String.valueOf(all.size()));
        statOccupiedBeds.setValue(String.valueOf(totalOcc));
        statAvailableBeds.setValue(String.valueOf(totalAvail));
        statFullRooms.setValue(String.valueOf(fullCount));

        applyFilters();
    }

    private void applyFilters() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Room> all = db.getAllRooms();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String block = blockFilter != null ? (String) blockFilter.getSelectedItem() : "All Blocks";
        String status = statusFilter != null ? (String) statusFilter.getSelectedItem() : "All Statuses";

        currentRoomList = all.stream().filter(r -> {
            boolean matchQuery = query.isEmpty()
                    || r.getRoomNumber().toLowerCase().contains(query)
                    || r.getBlock().toLowerCase().contains(query);

            boolean matchBlock = "All Blocks".equals(block) || r.getBlock().equalsIgnoreCase(block);
            boolean matchStatus = "All Statuses".equals(status) || r.getStatus().name().equalsIgnoreCase(status);

            return matchQuery && matchBlock && matchStatus;
        }).collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Room r : currentRoomList) {
            tableModel.addRow(new Object[]{
                    r.getRoomNumber(),
                    r.getBlock(),
                    "Floor " + r.getFloor(),
                    r.getRoomType(),
                    r.getCapacity() + " Beds",
                    r.getOccupied() + " Occupied",
                    r.getAvailableBeds() + " Vacant",
                    r.getStatus().name(),
                    "View"
            });
        }
    }

    public void openAllocateDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Allocate Resident to Room", "Assign an enrolled student to an available bed", "Confirm Allocation");

        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> students = db.getAllStudents();
        String[] stuOpts = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            stuOpts[i] = s.getName() + " (" + s.getStudentId() + ") - Currently: " + (s.getRoomNumber() != null ? s.getRoomNumber() : "No Room");
        }
        CustomComboBox<String> studentC = new CustomComboBox<>(stuOpts);

        List<Room> rooms = db.getAllRooms().stream()
                .filter(r -> r.getAvailableBeds() > 0)
                .toList();

        String[] roomOpts = new String[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            roomOpts[i] = r.getRoomNumber() + " (" + r.getBlock() + " - " + r.getAvailableBeds() + " beds vacant)";
        }
        CustomComboBox<String> roomC = new CustomComboBox<>(roomOpts);

        dialog.addFormField("Select Student *", studentC);
        dialog.addFormField("Select Destination Room (Vacant Beds Only) *", roomC);

        dialog.getPrimaryButton().addActionListener(e -> {
            if (students.isEmpty() || rooms.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No students or vacant rooms available.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int stuIdx = studentC.getSelectedIndex();
            int roomIdx = roomC.getSelectedIndex();

            Student chosenStudent = students.get(stuIdx);
            Room chosenRoom = rooms.get(roomIdx);

            boolean ok = db.allocateStudentToRoom(chosenStudent.getId(), chosenRoom.getId(), currentUser.getFullName(), currentUser.getRole().name());
            if (ok) {
                dialog.setConfirmed(true);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Could not complete allocation (room may have reached capacity).", "Allocation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.showModal(520, 360);
    }

    public void openTransferDialog() {
        openAllocateDialog(); // Transfer uses same robust workflow
    }

    private void openRoomDetailsDialog(Room room) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Room " + room.getRoomNumber() + " Details", room.getBlock() + " • " + room.getRoomType(), "Close");

        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> occupants = db.getAllStudents().stream()
                .filter(s -> s.getRoomId() == room.getId())
                .toList();

        JPanel occList = new JPanel();
        occList.setLayout(new BoxLayout(occList, BoxLayout.Y_AXIS));
        occList.setOpaque(false);

        if (occupants.isEmpty()) {
            JLabel empty = new JLabel("No occupants currently assigned to this room.");
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_MUTED);
            occList.add(empty);
        } else {
            for (Student s : occupants) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel name = new JLabel("<html><b>" + s.getName() + "</b> (" + s.getStudentId() + ")<br><font color='#64748b' size='2'>" + s.getDepartment() + " • " + s.getContact() + "</font></html>");
                name.setFont(UITheme.FONT_BODY);

                StatusBadge badge = new StatusBadge(s.getStatus().name(), UITheme.SUCCESS);
                row.add(name, BorderLayout.CENTER);
                row.add(badge, BorderLayout.EAST);
                occList.add(row);
            }
        }

        dialog.addFormField("Room Amenities", new JLabel(room.getAmenities() != null ? room.getAmenities() : "Standard Room Amenities"));
        dialog.addFormField("Current Room Occupants (" + occupants.size() + "/" + room.getCapacity() + " Beds)", occList);

        dialog.getPrimaryButton().addActionListener(e -> dialog.dispose());
        dialog.showModal(500, 420);
    }
}
