package com.hostel.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AppDialog extends JDialog {
    private boolean confirmed = false;
    private final JPanel bodyPanel;
    private final JPanel footerPanel;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final CustomButton primaryBtn;
    private final CustomButton cancelBtn;

    public AppDialog(Window parent, String title, String subtitle, String primaryBtnText) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.APP_BG);
        setResizable(true);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(8, 4));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titleContainer.add(titleLabel);
        if (subtitle != null && !subtitle.isEmpty()) {
            titleContainer.add(Box.createVerticalStrut(2));
            titleContainer.add(subtitleLabel);
        }
        headerPanel.add(titleContainer, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(18, 24, 18, 24)
        ));

        // Body
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // Footer
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footerPanel.setBackground(new Color(248, 250, 252));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        cancelBtn = new CustomButton("Cancel", CustomButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        primaryBtn = new CustomButton(primaryBtnText != null ? primaryBtnText : "Save Changes", CustomButton.Variant.PRIMARY);

        footerPanel.add(cancelBtn);
        footerPanel.add(primaryBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public JPanel getBodyPanel() {
        return bodyPanel;
    }

    public CustomButton getPrimaryButton() {
        return primaryBtn;
    }

    public CustomButton getCancelButton() {
        return cancelBtn;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public JPanel addFormField(String labelText, JComponent component) {
        JPanel fieldGroup = new JPanel(new BorderLayout(0, 6));
        fieldGroup.setOpaque(false);
        fieldGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        fieldGroup.add(lbl, BorderLayout.NORTH);
        fieldGroup.add(component, BorderLayout.CENTER);
        fieldGroup.setBorder(new EmptyBorder(0, 0, 14, 0));

        bodyPanel.add(fieldGroup);
        return fieldGroup;
    }

    public JPanel addTwoColumnRow(String label1, JComponent comp1, String label2, JComponent comp2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel col1 = new JPanel(new BorderLayout(0, 6));
        col1.setOpaque(false);
        JLabel lbl1 = new JLabel(label1);
        lbl1.setFont(UITheme.FONT_BODY_BOLD);
        lbl1.setForeground(UITheme.TEXT_SECONDARY);
        col1.add(lbl1, BorderLayout.NORTH);
        col1.add(comp1, BorderLayout.CENTER);

        JPanel col2 = new JPanel(new BorderLayout(0, 6));
        col2.setOpaque(false);
        JLabel lbl2 = new JLabel(label2);
        lbl2.setFont(UITheme.FONT_BODY_BOLD);
        lbl2.setForeground(UITheme.TEXT_SECONDARY);
        col2.add(lbl2, BorderLayout.NORTH);
        col2.add(comp2, BorderLayout.CENTER);

        row.add(col1);
        row.add(col2);

        bodyPanel.add(row);
        return row;
    }

    public void showModal(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
}
