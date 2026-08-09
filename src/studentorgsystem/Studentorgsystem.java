package studentorgsystem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.concurrent.TimeUnit;

public class Studentorgsystem {

    private static final Logger LOG = Logger.getLogger(Studentorgsystem.class.getName());

    static final String ADMIN_USERNAME      = "admin";
    static final String ADMIN_PASSWORD_HASH = PasswordUtils.hash("admin123");

    public static void main(String[] args) {
        UITheme.applyGlobalDefaults();
        BackupManager.createBootBackup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // ============================================================
    //  GOOGLE MATERIAL DESIGN THEME
    // ============================================================
    static class UITheme {

        static final int R_BTN   = 24;
        static final int R_CARD  = 16;
        static final int R_INPUT = 10;
        static final int R_CHIP  = 20;
        static final int R_TABLE = 12;

        static final Color GOOGLE_BLUE    = new Color(26,  115, 232);
        static final Color GOOGLE_BLUE_DK = new Color(10,  88,  202);
        static final Color GOOGLE_RED     = new Color(234, 67,  53);
        static final Color GOOGLE_GREEN   = new Color(52,  168, 83);
        static final Color GOOGLE_YELLOW  = new Color(251, 188, 4);

        static final Color BG          = new Color(241, 243, 244);
        static final Color SURFACE     = Color.WHITE;
        static final Color SURFACE_ALT = new Color(248, 249, 250);

        static final Color TEXT_PRIMARY   = new Color(32,  33,  36);
        static final Color TEXT_SECONDARY = new Color(95,  99,  104);
        static final Color TEXT_DISABLED  = new Color(189, 193, 198);

        static final Color BORDER  = new Color(218, 220, 224);
        static final Color DIVIDER = new Color(232, 234, 237);

        static final Color BLUE_LIGHT   = new Color(232, 240, 254);
        static final Color GREEN_LIGHT  = new Color(230, 244, 234);
        static final Color RED_LIGHT    = new Color(252, 232, 230);
        static final Color YELLOW_LIGHT = new Color(254, 247, 224);
        static final Color ORANGE       = new Color(250, 100, 0);

        static final Color EV_UPCOMING_BG   = new Color(26,  115, 232);
        static final Color EV_HAPPENING_BG  = new Color(234, 67,  53);
        static final Color EV_COMPLETED_BG  = new Color(95,  99,  104);
        static final Color EV_UPCOMING_FG   = Color.WHITE;
        static final Color EV_HAPPENING_FG  = Color.WHITE;
        static final Color EV_COMPLETED_FG  = Color.WHITE;

        static final String BASE_FONT = pickFont(
            "Roboto", "Google Sans", "Segoe UI", "Helvetica Neue", "Arial", "SansSerif");

        static String pickFont(String... candidates) {
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            java.util.Set<String> avail = new java.util.HashSet<>(
                java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));
            for (String f : candidates) if (avail.contains(f)) return f;
            return "SansSerif";
        }

        static final Font FONT_HEADING    = new Font(BASE_FONT, Font.BOLD,  22);
        static final Font FONT_SUBHEADING = new Font(BASE_FONT, Font.BOLD,  15);
        static final Font FONT_BODY       = new Font(BASE_FONT, Font.PLAIN, 13);
        static final Font FONT_BODY_BOLD  = new Font(BASE_FONT, Font.BOLD,  13);
        static final Font FONT_CAPTION    = new Font(BASE_FONT, Font.PLAIN, 11);
        static final Font FONT_MONO       = new Font("Monospaced", Font.PLAIN, 13);
        static final Font FONT_BUTTON     = new Font(BASE_FONT, Font.BOLD,  13);
        static final Font FONT_TABLE_HDR  = new Font(BASE_FONT, Font.BOLD,  12);
        static final Font FONT_TABLE_BODY = new Font(BASE_FONT, Font.PLAIN, 12);

        static void applyGlobalDefaults() {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            UIManager.put("Button.font",           FONT_BUTTON);
            UIManager.put("Label.font",            FONT_BODY);
            UIManager.put("TextField.font",        FONT_BODY);
            UIManager.put("PasswordField.font",    FONT_BODY);
            UIManager.put("ComboBox.font",         FONT_BODY);
            UIManager.put("Table.font",            FONT_TABLE_BODY);
            UIManager.put("TableHeader.font",      FONT_TABLE_HDR);
            UIManager.put("TabbedPane.font",       FONT_BODY_BOLD);
            UIManager.put("Panel.background",      SURFACE);
            UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
            UIManager.put("OptionPane.messageFont",FONT_BODY);
            UIManager.put("MenuItem.font",         FONT_BODY);
            UIManager.put("Menu.font",             FONT_BODY_BOLD);
            UIManager.put("TextArea.font",         FONT_BODY);
            UIManager.put("List.font",             FONT_BODY);
            UIManager.put("Spinner.font",          FONT_BODY);
            UIManager.put("TitledBorder.font",     FONT_CAPTION);
        }

        static class RoundedBorder extends javax.swing.border.AbstractBorder {
            private final Color color; private final int radius; private final int thickness;
            RoundedBorder(Color c, int r, int t) { color=c; radius=r; thickness=t; }
            @Override public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(thickness));
                g2.drawRoundRect(x+thickness/2, y+thickness/2, w-thickness, h-thickness, radius, radius);
                g2.dispose();
            }
            @Override public Insets getBorderInsets(Component c) { int i=radius/4+thickness; return new Insets(i,i,i,i); }
            @Override public Insets getBorderInsets(Component c, Insets in) { int i=radius/4+thickness; in.set(i,i,i,i); return in; }
        }

        static class RoundedPanel extends JPanel {
            private final int radius; private final Color bg; private final Color borderColor;
            RoundedPanel(LayoutManager lm, int r, Color background, Color border) {
                super(lm); radius=r; bg=background; borderColor=border;
                setOpaque(false);
            }
            RoundedPanel(int r, Color background) { this(new BorderLayout(), r, background, null); }
            RoundedPanel(int r, Color background, Color border) { this(new BorderLayout(), r, background, border); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                if (borderColor != null) {
                    g2.setColor(borderColor);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
                }
                g2.dispose();
            }
        }

        static class RoundedButton extends JButton {
            private Color normalBg, hoverBg, pressBg;
            private boolean outlined = false;
            private Color outlineColor;

            RoundedButton(String text, Color normal, Color hover, Color press, Color fg) {
                super(text); normalBg=normal; hoverBg=hover; pressBg=press;
                setForeground(fg); setFont(FONT_BUTTON);
                setUI(new javax.swing.plaf.basic.BasicButtonUI());
                setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
                setOpaque(false); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent  e) { repaint(); }
                    public void mousePressed(java.awt.event.MouseEvent e) { repaint(); }
                    public void mouseReleased(java.awt.event.MouseEvent e){ repaint(); }
                });
            }

            void makeOutlined(Color fg, Color border) {
                outlined=true; outlineColor=border; normalBg=SURFACE; hoverBg=BLUE_LIGHT; pressBg=new Color(210,230,255);
                setForeground(fg);
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill;
                if (!isEnabled())                fill = new Color(220,222,225);
                else if (getModel().isPressed())  fill = pressBg;
                else if (getModel().isRollover()) fill = hoverBg;
                else                              fill = normalBg;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), R_BTN, R_BTN);
                if (outlined && outlineColor != null) {
                    g2.setColor(getModel().isRollover() ? outlineColor.darker() : outlineColor);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, R_BTN, R_BTN);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        }

        static JButton primaryBtn(String text) {
            return new RoundedButton(text, GOOGLE_BLUE, GOOGLE_BLUE_DK, new Color(5,70,180), Color.WHITE);
        }
        static JButton dangerBtn(String text) {
            return new RoundedButton(text, GOOGLE_RED, new Color(198,40,40), new Color(170,20,20), Color.WHITE);
        }
        static JButton successBtn(String text) {
            return new RoundedButton(text, GOOGLE_GREEN, new Color(30,130,55), new Color(15,100,40), Color.WHITE);
        }
        static JButton warningBtn(String text) {
            return new RoundedButton(text, ORANGE, new Color(200,70,0), new Color(160,50,0), Color.WHITE);
        }
        static JButton ghostBtn(String text) {
            RoundedButton b = new RoundedButton(text, SURFACE, BG, DIVIDER, TEXT_SECONDARY);
            return b;
        }
        static JButton outlinedBtn(String text) {
            RoundedButton b = new RoundedButton(text, SURFACE, BLUE_LIGHT, new Color(210,230,255), GOOGLE_BLUE);
            b.makeOutlined(GOOGLE_BLUE, GOOGLE_BLUE);
            return b;
        }

        static void roundField(JTextField f) {
            f.setFont(FONT_BODY); f.setBackground(SURFACE); f.setForeground(TEXT_PRIMARY);
            f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, R_INPUT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            f.setOpaque(true);
        }

        static JTextField styledField() { JTextField f=new JTextField(); roundField(f); return f; }

        static JPasswordField styledPasswordField() {
            JPasswordField f = new JPasswordField();
            f.setFont(FONT_BODY); f.setBackground(SURFACE); f.setForeground(TEXT_PRIMARY);
            f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, R_INPUT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            return f;
        }

        static JPanel card() {
            JPanel p = new RoundedPanel(new BorderLayout(), R_CARD, SURFACE, BORDER);
            p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            return p;
        }

        static JPanel sectionHeader(String title) {
            JPanel p = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(32,33,36));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()+R_CARD,R_CARD,R_CARD);
                    g2.dispose();
                }
            };
            p.setOpaque(false);
            p.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
            JLabel l = new JLabel(title); l.setFont(FONT_SUBHEADING); l.setForeground(Color.WHITE);
            p.add(l, BorderLayout.WEST);
            return p;
        }

        static void styleTableHeader(JTable t, Color bg) {
            t.getTableHeader().setReorderingAllowed(false);
            t.getTableHeader().setDefaultRenderer(
                (table, value, isSelected, hasFocus, row, col) -> {
                    JPanel cell = new JPanel(new BorderLayout());
                    cell.setBackground(bg);
                    cell.setOpaque(true);
                    cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, bg.darker()),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                    JLabel lbl = new JLabel(value == null ? "" : value.toString());
                    lbl.setFont(FONT_TABLE_HDR);
                    lbl.setForeground(Color.WHITE);
                    lbl.setBackground(bg);
                    lbl.setOpaque(false);
                    cell.add(lbl, BorderLayout.CENTER);
                    return cell;
                }
            );
        }

        static void styleTable(JTable t) {
            t.setFont(FONT_TABLE_BODY);
            t.setRowHeight(30);
            t.setGridColor(DIVIDER);
            t.setShowGrid(true);
            t.setIntercellSpacing(new Dimension(0, 0));
            t.setSelectionBackground(BLUE_LIGHT);
            t.setSelectionForeground(TEXT_PRIMARY);
            styleTableHeader(t, new Color(32, 33, 36));
        }

        static JPanel roundedTableWrapper(JScrollPane sp) {
            JPanel wrap = new RoundedPanel(new BorderLayout(), R_TABLE, SURFACE, BORDER);
            sp.setBorder(null); sp.setBackground(SURFACE);
            sp.getViewport().setBackground(SURFACE);
            wrap.add(sp);
            return wrap;
        }

        static JLabel chip(String text, Color bg, Color fg) {
            JLabel l = new JLabel(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),R_CHIP,R_CHIP);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            l.setFont(new Font(BASE_FONT, Font.BOLD, 11));
            l.setBackground(bg); l.setForeground(fg); l.setOpaque(false);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
            return l;
        }

        static JPanel toolbarPanel() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
            p.setBackground(SURFACE);
            p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, DIVIDER),
                BorderFactory.createEmptyBorder(2,8,2,8)));
            return p;
        }

        static JSeparator divider() { JSeparator s=new JSeparator(); s.setForeground(DIVIDER); return s; }

        static Icon eyeIcon(boolean visible) {
            int S = 22;
            BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0,0,0,0)); g.fillRect(0,0,S,S);
            g.setColor(visible ? GOOGLE_BLUE : TEXT_SECONDARY);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(1, 6, S-2, S-8, 0, 180); g.drawArc(1, 6, S-2, S-8, 180, 180);
            g.drawOval(S/2-4, S/2-4, 8, 8);
            g.fillOval(S/2-2, S/2-2, 5, 5);
            if (!visible) {
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(GOOGLE_RED); g.drawLine(3, S-4, S-3, 4);
            }
            g.dispose();
            return new ImageIcon(img);
        }
    }

    // ============================================================
    //  REAL-TIME REFRESH BUS
    // ============================================================
    interface AttendanceRefreshListener { void onAttendanceChanged(); }
    static volatile AttendanceRefreshListener globalAttendanceListener = null;

    static void notifyAttendanceChanged() {
        AttendanceRefreshListener l = globalAttendanceListener;
        if (l != null) SwingUtilities.invokeLater(l::onAttendanceChanged);
    }

    static void notifyEventChanged() {
        DataCache.invalidateEvents();
        notifyAttendanceChanged();
    }

    // ============================================================
    //  ENUMS
    // ============================================================
    enum MemberStatus {
        ACTIVE("Active"), INACTIVE("Inactive"), GRADUATED("Graduated");
        private final String label;
        MemberStatus(String l) { this.label = l; }
        public String getLabel() { return label; }
        public static String[] labels() {
            MemberStatus[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].label; return a;
        }
    }

    enum EventStatus {
        UPCOMING("Upcoming"), HAPPENING("Happening"), COMPLETED("Completed");
        private final String label;
        EventStatus(String l) { this.label = l; }
        public String getLabel() { return label; }
        public static String[] labels() {
            EventStatus[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].label; return a;
        }
    }

    enum EventType {
        GENERAL_ASSEMBLY("General Assembly"), WORKSHOP("Workshop"),
        FUNDRAISER("Fundraiser"), TEAM_BUILDING("Team Building"),
        SEMINAR("Seminar"), SPORTS_FEST("Sports Fest"), OTHER("Other");
        private final String label;
        EventType(String l) { this.label = l; }
        public String getLabel() { return label; }
        public static String[] labels() {
            EventType[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].label; return a;
        }
    }

    enum MemberPosition {
        MEMBER("Member"), OFFICER("Officer"), PRESIDENT("President"),
        VICE_PRESIDENT("Vice President"), SECRETARY("Secretary"),
        TREASURER("Treasurer"), AUDITOR("Auditor");
        private final String label;
        MemberPosition(String l) { this.label = l; }
        public String getLabel() { return label; }
        public static String[] labels() {
            MemberPosition[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].label; return a;
        }
    }

    enum Course {
        CTE, CCS, CMBS, CPC, CAH, CABE, CEA, CN, CM, CHEFS;
        public static String[] labels() {
            Course[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].name(); return a;
        }
    }

    enum YearLevel {
        FIRST("1st Year"), SECOND("2nd Year"), THIRD("3rd Year"),
        FOURTH("4th Year"), FIFTH("5th Year");
        private final String label;
        YearLevel(String l) { this.label = l; }
        public String getLabel() { return label; }
        public static String[] labels() {
            YearLevel[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].label; return a;
        }
    }

    enum TransactionType {
        INCOME("Income"), EXPENSE("Expense");
        private final String label;
        TransactionType(String l) { this.label = l; }
        public String getLabel() { return label; }
        public static String[] labels() {
            TransactionType[] v = values(); String[] a = new String[v.length];
            for (int i = 0; i < v.length; i++) a[i] = v[i].label; return a;
        }
    }

    // ============================================================
    //  VALIDATION RESULT
    // ============================================================
    static class ValidationResult {
        private final boolean valid; private final String message;
        private ValidationResult(boolean v, String m) { valid = v; message = m; }
        static ValidationResult ok()             { return new ValidationResult(true,  null); }
        static ValidationResult fail(String msg) { return new ValidationResult(false, msg);  }
        boolean isValid()    { return valid;   }
        String  getMessage() { return message; }
    }

    // ============================================================
    //  SECURITY
    // ============================================================
    static class PasswordUtils {
        static String hash(String plain) {
            try {
                MessageDigest d = MessageDigest.getInstance("SHA-256");
                byte[] b = d.digest(plain.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte x : b) sb.append(String.format("%02x", x));
                return sb.toString();
            } catch (NoSuchAlgorithmException e) { throw new RuntimeException("SHA-256 not available", e); }
        }
        static boolean verify(String plain, String stored) { return hash(plain).equals(stored); }
    }

    // ============================================================
    //  DATA MODELS
    // ============================================================
    static class Member {
        final String studentId, name, course, yearLevel, position, status;
        Member(String sid, String n, String c, String y, String p, String s) {
            studentId=sid; name=n; course=c; yearLevel=y; position=p; status=s;
        }
    }

    static class Event {
        final String eventId, name, date, type, description, venue, status;
        Event(String eid, String n, String d, String t, String desc, String v, String s) {
            eventId=eid; name=n; date=d; type=t; description=desc; venue=v; status=s;
        }
        boolean isCompleted()  { return EventStatus.COMPLETED.getLabel().equals(status);  }
        boolean isHappening()  { return EventStatus.HAPPENING.getLabel().equals(status);  }
        boolean isUpcoming()   { return EventStatus.UPCOMING.getLabel().equals(status);   }
        boolean allowCheckIn() { return isHappening(); }
    }

    /**
     * UPDATED FinanceTransaction model.
     *
     * New field: {@code eventId}
     *   – INCOME rows: the event this budget belongs to (required).
     *   – EXPENSE rows: the event the cost is charged to (required; set automatically
     *     by AddExpenseDialog so the user never has to type it).
     *
     * JSON structure (finances.json):
     * [
     *   { "txId":"TX-1", "eventId":"EVT-1", "date":"2025-01-01",
     *     "description":"Budget allocation", "type":"Income", "amount":5000.00 },
     *   { "txId":"TX-2", "eventId":"EVT-1", "date":"2025-01-05",
     *     "description":"Venue rental",      "type":"Expense","amount":1200.00 }
     * ]
     */
    static class FinanceTransaction {
        final String txId, eventId, date, description, type;
        final double amount;
        FinanceTransaction(String id, String eventId, String d, String desc, String t, double a) {
            txId=id; this.eventId=(eventId!=null?eventId:""); date=d; description=desc; type=t; amount=a;
        }
    }

    // ============================================================
    //  CACHE
    // ============================================================
    static class DataCache {
        private static List<Member>             memberCache  = null;
        private static List<Event>              eventCache   = null;
        private static List<JSONObject>         attendCache  = null;
        private static List<FinanceTransaction> financeCache = null;
        static List<Member>             getMembers()    { return memberCache;  }
        static List<Event>              getEvents()     { return eventCache;   }
        static List<JSONObject>         getAttendance() { return attendCache;  }
        static List<FinanceTransaction> getFinances()   { return financeCache; }
        static void setMembers(List<Member> d)              { memberCache  = d; }
        static void setEvents(List<Event> d)                { eventCache   = d; }
        static void setAttendance(List<JSONObject> d)       { attendCache  = d; }
        static void setFinances(List<FinanceTransaction> d) { financeCache = d; }
        static void invalidateMembers()    { memberCache  = null; }
        static void invalidateEvents()     { eventCache   = null; }
        static void invalidateAttendance() { attendCache  = null; }
        static void invalidateFinances()   { financeCache = null; }
        static void invalidateAll() { invalidateMembers(); invalidateEvents(); invalidateAttendance(); invalidateFinances(); }
    }

    // ============================================================
    //  FILE UTILITIES
    // ============================================================
    static String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            int ch; while ((ch = r.read()) != -1) sb.append((char) ch);
        }
        return sb.toString();
    }
    static void writeFile(String path, String content) throws IOException {
        File target = new File(path), temp = new File(path + ".tmp");
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8))) { w.write(content); }
        if (target.exists() && !target.delete()) throw new IOException("Cannot replace: " + path);
        if (!temp.renameTo(target))               throw new IOException("Cannot rename to: " + path);
    }
    static String s(JSONObject j, String key) { Object v = j.get(key); return v != null ? v.toString() : ""; }
    static String csvQuote(String v) { if (v == null) return "\"\""; return "\"" + v.replace("\"", "\"\"") + "\""; }
    static String[] prependAll(String[] labels) {
        String[] r = new String[labels.length + 1]; r[0] = "All Status";
        System.arraycopy(labels, 0, r, 1, labels.length); return r;
    }
    static void showSaveError(String name) {
        JOptionPane.showMessageDialog(null, "Failed to save " + name + " data.\nCheck logs.", "Save Error", JOptionPane.ERROR_MESSAGE);
    }

    // ============================================================
    //  CHECK-IN VALIDATION  — centralised
    // ============================================================
    static ValidationResult validateCheckIn(Event event, boolean adminOverride) {
        if (event.isUpcoming()) {
            return ValidationResult.fail(
                "Check-in is only allowed for ongoing (Happening) events.\n" +
                "\"" + event.name + "\" has not started yet.\n\n" +
                "The status will automatically change to Happening on " + event.date + ".");
        }
        if (event.isCompleted() && !adminOverride) {
            return ValidationResult.fail(
                "\"" + event.name + "\" is already Completed.\n" +
                "Check-in is disabled for completed events.\n\n" +
                "Use Admin Override in the Events panel if you need to edit attendance.");
        }
        return ValidationResult.ok();
    }

    // ============================================================
    //  MEMBER MANAGER
    // ============================================================
    static class MemberManager {
        static final String FILE = "members.json", BACKUP_FILE = "members_backup.json";

        static List<Member> loadAll() {
            if (DataCache.getMembers() != null) return DataCache.getMembers();
            List<Member> list = new ArrayList<>();
            if (!new File(FILE).exists()) return list;
            try {
                JSONArray arr = (JSONArray) new JSONParser().parse(readFile(FILE));
                for (Object o : arr) {
                    JSONObject j = (JSONObject) o;
                    list.add(new Member(s(j,"studentId"),s(j,"name"),s(j,"course"),
                                        s(j,"yearLevel"),s(j,"position"),s(j,"status")));
                }
            } catch (ParseException e) { LOG.log(Level.WARNING,"Member parse error",e);
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Member read error",e); }
            DataCache.setMembers(list);
            return list;
        }

        @SuppressWarnings("unchecked")
        static void saveAll(List<Member> list) {
            JSONArray arr = new JSONArray();
            for (Member m : list) {
                JSONObject j = new JSONObject();
                j.put("studentId",m.studentId); j.put("name",m.name);
                j.put("course",m.course); j.put("yearLevel",m.yearLevel);
                j.put("position",m.position); j.put("status",m.status);
                arr.add(j);
            }
            String c = arr.toJSONString();
            try {
                new JSONParser().parse(c);
                writeFile(FILE,c);
                writeFile(BACKUP_FILE,c);
                DataCache.invalidateMembers();          // always flush member cache
            } catch (ParseException e) { LOG.log(Level.SEVERE,"Bad member JSON",e); showSaveError("member");
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Member write error",e); showSaveError("member"); }
        }

        static void add(Member m) {
            List<Member> l = new ArrayList<>(loadAll());
            l.add(m);
            saveAll(l);
        }

        static void edit(String id, Member u) {
            List<Member> l = new ArrayList<>(loadAll());
            for (int i = 0; i < l.size(); i++) if (l.get(i).studentId.equals(id)) { l.set(i,u); break; }
            saveAll(l);
        }

        /**
         * FIX 1 — CASCADE DELETE
         * Deleting a member MUST remove every attendance record that references
         * their Student ID.  Without this step orphaned IDs accumulate in
         * attendance.json and show up as "(unknown)" in the check-in log.
         *
         * Sequence:
         *   1. Remove from members.json  → invalidates member cache
         *   2. Remove from attendance.json via deleteMemberAttendance()
         *      → invalidates attendance cache
         *   3. Notify UI listeners so panels refresh immediately
         */
        static void delete(String id) {
            // Step 1: remove from members.json
            List<Member> l = new ArrayList<>(loadAll());
            l.removeIf(m -> m.studentId.equals(id));
            saveAll(l);                                         // also calls DataCache.invalidateMembers()

            // Step 2: cascade — purge every attendance record that references this ID
            AttendanceManager.deleteMemberAttendance(id);      // NEW cascade call

            // Step 3: broadcast so the Members panel attendance column refreshes
            notifyAttendanceChanged();
        }

        /** Always reads from the freshly-invalidated cache — never stale. */
        static boolean idExists(String id) {
            // Force fresh read: cache was invalidated by saveAll() on the last delete/add
            for (Member m : loadAll()) if (m.studentId.equalsIgnoreCase(id)) return true;
            return false;
        }

        static boolean nameExists(String n, String excl) {
            for (Member m : loadAll()) if (m.name.equalsIgnoreCase(n) && !m.studentId.equalsIgnoreCase(excl)) return true;
            return false;
        }

        static List<Member> search(String kw) {
            String k=kw.toLowerCase(); List<Member> r=new ArrayList<>();
            for (Member m : loadAll())
                if (m.studentId.toLowerCase().contains(k) ||
                    m.name.toLowerCase().contains(k)      ||
                    m.course.toLowerCase().contains(k))   r.add(m);
            return r;
        }

        static void exportCSV() {
            JFileChooser fc=new JFileChooser(); fc.setSelectedFile(new File("members_export.csv"));
            if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile()), StandardCharsets.UTF_8))) {
                bw.write("Student ID,Name,Course,Year Level,Position,Status"); bw.newLine();
                for (Member m : loadAll()) {
                    bw.write(csvQuote(m.studentId)+","+csvQuote(m.name)+","+csvQuote(m.course)+","+
                             csvQuote(m.yearLevel)+","+csvQuote(m.position)+","+csvQuote(m.status));
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(null,"Exported to: "+fc.getSelectedFile(),"Export OK",JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) { LOG.log(Level.SEVERE,"CSV export failed",e); JOptionPane.showMessageDialog(null,"Export failed: "+e.getMessage()); }
        }
    }

    // ============================================================
    //  EVENT MANAGER
    // ============================================================
    static class EventManager {
        static final String FILE = "events.json", BACKUP_FILE = "events_backup.json";

        static String computeAutoStatus(String dateStr) {
            if (dateStr == null || dateStr.isEmpty()) return EventStatus.UPCOMING.getLabel();
            try {
                String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                int cmp = dateStr.compareTo(today);
                if (cmp < 0)  return EventStatus.COMPLETED.getLabel();
                if (cmp == 0) return EventStatus.HAPPENING.getLabel();
                return EventStatus.UPCOMING.getLabel();
            } catch (Exception e) { return EventStatus.UPCOMING.getLabel(); }
        }

        static List<Event> loadAll() {
            if (DataCache.getEvents() != null) return DataCache.getEvents();
            List<Event> list = new ArrayList<>();
            if (!new File(FILE).exists()) return list;
            try {
                JSONArray arr = (JSONArray) new JSONParser().parse(readFile(FILE));
                for (Object o : arr) {
                    JSONObject j = (JSONObject) o;
                    String dateStr    = s(j,"date");
                    String stored     = s(j,"status");
                    String autoStatus = computeAutoStatus(dateStr);
                    String finalStatus = (EventStatus.COMPLETED.getLabel().equals(stored)
                            && EventStatus.UPCOMING.getLabel().equals(autoStatus))
                            ? stored : autoStatus;
                    list.add(new Event(s(j,"eventId"),s(j,"name"),dateStr,s(j,"type"),
                                       s(j,"description"),s(j,"venue"),finalStatus));
                }
            } catch (ParseException e) { LOG.log(Level.WARNING,"Event parse error",e);
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Event read error",e); }
            DataCache.setEvents(list);
            return list;
        }

        @SuppressWarnings("unchecked")
        static void saveAll(List<Event> list) {
            JSONArray arr = new JSONArray();
            for (Event e : list) {
                JSONObject j = new JSONObject();
                j.put("eventId",e.eventId); j.put("name",e.name); j.put("date",e.date);
                j.put("type",e.type); j.put("description",e.description);
                j.put("venue",e.venue); j.put("status",e.status);
                arr.add(j);
            }
            String c = arr.toJSONString();
            try {
                new JSONParser().parse(c);
                writeFile(FILE,c);
                writeFile(BACKUP_FILE,c);
                DataCache.invalidateEvents();
            } catch (ParseException e) { LOG.log(Level.SEVERE,"Bad event JSON",e); showSaveError("event");
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Event write error",e); showSaveError("event"); }
        }

        static void add(Event e)         { List<Event> l=new ArrayList<>(loadAll()); l.add(e); saveAll(l); notifyEventChanged(); }
        static void edit(String id, Event u) {
            List<Event> l=new ArrayList<>(loadAll());
            for (int i=0;i<l.size();i++) if (l.get(i).eventId.equals(id)) { l.set(i,u); break; }
            saveAll(l); notifyEventChanged();
        }
        static void delete(String id) {
            List<Event> l=new ArrayList<>(loadAll()); l.removeIf(e->e.eventId.equals(id)); saveAll(l);
            AttendanceManager.deleteEventAttendance(id); notifyEventChanged();
        }
        static String generateId()             { return "EVT-"+System.currentTimeMillis(); }
        static boolean eventNameExists(String name, String excl) {
            for (Event e : loadAll()) if (e.name.equalsIgnoreCase(name) && !e.eventId.equals(excl)) return true;
            return false;
        }
        static List<Event> search(String kw) {
            String k=kw.toLowerCase(); List<Event> r=new ArrayList<>();
            for (Event e : loadAll())
                if (e.name.toLowerCase().contains(k)   || e.type.toLowerCase().contains(k) ||
                    e.venue.toLowerCase().contains(k)  || e.status.toLowerCase().contains(k)) r.add(e);
            return r;
        }
        static Event findById(String eventId) {
            for (Event e : loadAll()) if (e.eventId.equals(eventId)) return e;
            return null;
        }
        static void refreshAutoStatuses() {
            List<Event> cached = DataCache.getEvents(); if (cached == null) return;
            boolean changed = false;
            for (Event e : cached) {
                String expected = computeAutoStatus(e.date);
                if (!expected.equals(e.status) &&
                    !(EventStatus.COMPLETED.getLabel().equals(e.status) && EventStatus.UPCOMING.getLabel().equals(expected))) {
                    changed = true; break;
                }
            }
            if (changed) { DataCache.invalidateEvents(); notifyEventChanged(); }
        }
    }

    // ============================================================
    //  ATTENDANCE MANAGER
    // ============================================================
    static class AttendanceManager {
        static final String FILE = "attendance.json", BACKUP_FILE = "attendance_backup.json";

        /**
         * Returns only the attendees for an event whose Student IDs still exist
         * in members.json.
         *
         * FIX 2 — ORPHAN GUARD ON READ
         * Even if a cascading delete somehow missed an entry (e.g. the app
         * crashed mid-write), this method will silently skip and auto-purge any
         * ID that no longer maps to a real member.  The file is rewritten
         * without those IDs so future reads are always clean.
         */
        static List<String> getAttendees(String eventId) {
            List<String> raw = getRawAttendees(eventId);
            List<String> clean = new ArrayList<>();
            boolean hadOrphans = false;
            for (String sid : raw) {
                if (MemberManager.idExists(sid)) {
                    clean.add(sid);
                } else {
                    hadOrphans = true;
                    LOG.log(Level.WARNING,
                        "Orphaned attendee ID ''{0}'' removed from event ''{1}'' during read-time cleanup.",
                        new Object[]{sid, eventId});
                }
            }
            // Auto-repair: rewrite without the orphaned IDs
            if (hadOrphans) {
                saveAttendance(eventId, clean);
            }
            return clean;
        }

        /** Raw read — bypasses orphan check.  Internal use only. */
        private static List<String> getRawAttendees(String eventId) {
            for (JSONObject rec : loadAllRecords()) {
                if (eventId.equals(s(rec,"eventId"))) {
                    JSONArray arr = (JSONArray) rec.get("attendees");
                    List<String> list = new ArrayList<>();
                    if (arr != null) for (Object id : arr) list.add(id.toString());
                    return list;
                }
            }
            return new ArrayList<>();
        }

        static List<String> getAttendedEventIds(String studentId) {
            List<String> result = new ArrayList<>();
            for (JSONObject rec : loadAllRecords()) {
                JSONArray arr = (JSONArray) rec.get("attendees");
                if (arr != null && arr.contains(studentId)) result.add(s(rec,"eventId"));
            }
            return result;
        }

        static boolean isLocked(String eventId) {
            Event ev = EventManager.findById(eventId);
            return ev != null && ev.isCompleted();
        }

        @SuppressWarnings("unchecked")
        static void deleteEventAttendance(String eventId) {
            List<JSONObject> records = new ArrayList<>(loadAllRecords());
            int before = records.size();
            records.removeIf(r -> eventId.equals(s(r,"eventId")));
            if (records.size() == before) return;
            JSONArray root = new JSONArray();
            for (JSONObject r : records) root.add(r);
            String c = root.toJSONString();
            try {
                new JSONParser().parse(c);
                writeFile(FILE,c); writeFile(BACKUP_FILE,c);
                DataCache.invalidateAttendance();
            } catch (ParseException e) { LOG.log(Level.SEVERE,"Bad attend JSON cascade-event-delete",e);
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Attend write error cascade-event-delete",e); }
        }

        /**
         * FIX 1 (implementation) — CASCADE DELETE FOR MEMBER
         *
         * Called by MemberManager.delete() immediately after a member is removed
         * from members.json.  Iterates every attendance record (every event) and
         * removes the studentId from the "attendees" array.
         *
         * Guarantees:
         *   • No orphaned IDs remain in attendance.json after this call.
         *   • Attendance cache is invalidated so subsequent reads are fresh.
         *   • The file is only rewritten if at least one change was made
         *     (avoids unnecessary I/O for members who never attended anything).
         */
        @SuppressWarnings("unchecked")
        static void deleteMemberAttendance(String studentId) {
            List<JSONObject> records = new ArrayList<>(loadAllRecords());
            boolean anyChanged = false;

            for (JSONObject rec : records) {
                JSONArray arr = (JSONArray) rec.get("attendees");
                if (arr != null && arr.remove(studentId)) {
                    anyChanged = true;
                    LOG.log(Level.INFO,
                        "Cascade delete: removed studentId ''{0}'' from event ''{1}'' attendance.",
                        new Object[]{studentId, s(rec,"eventId")});
                }
            }

            if (!anyChanged) {
                // Member had no attendance records — nothing to clean up.
                return;
            }

            JSONArray root = new JSONArray();
            for (JSONObject r : records) root.add(r);
            String c = root.toJSONString();
            try {
                new JSONParser().parse(c);
                writeFile(FILE,c);
                writeFile(BACKUP_FILE,c);
                DataCache.invalidateAttendance();   // force fresh reads everywhere
            } catch (ParseException e) { LOG.log(Level.SEVERE,"Bad attend JSON member-cascade-delete",e);
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Attend write error member-cascade-delete",e); }
        }

        @SuppressWarnings("unchecked")
        static void saveAttendance(String eventId, List<String> attendees) {
            // Deduplicate and — before saving — drop any IDs not in members.json
            List<String> deduped = new ArrayList<>(new LinkedHashSet<>(attendees));
            deduped.removeIf(sid -> !MemberManager.idExists(sid));  // FIX: never persist invalid IDs

            List<JSONObject> records = new ArrayList<>(loadAllRecords());
            records.removeIf(r -> eventId.equals(s(r,"eventId")));
            JSONObject rec = new JSONObject();
            JSONArray arr = new JSONArray();
            for (String id : deduped) arr.add(id);
            rec.put("eventId",eventId);
            rec.put("attendees",arr);
            records.add(rec);

            JSONArray root = new JSONArray();
            for (JSONObject r : records) root.add(r);
            String c = root.toJSONString();
            try {
                new JSONParser().parse(c);
                writeFile(FILE,c); writeFile(BACKUP_FILE,c);
                DataCache.invalidateAttendance();
            } catch (ParseException e) { LOG.log(Level.SEVERE,"Bad attend JSON",e); showSaveError("attendance");
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Attend write error",e); showSaveError("attendance"); }
        }

        static int countAttended(String sid)  {
            int c=0;
            for (JSONObject rec : loadAllRecords()) {
                JSONArray arr = (JSONArray) rec.get("attendees");
                if (arr != null && arr.contains(sid)) c++;
            }
            return c;
        }
        static String attendanceCount(String sid) { return countAttended(sid)+"/"+EventManager.loadAll().size(); }

        private static List<JSONObject> loadAllRecords() {
            if (DataCache.getAttendance() != null) return DataCache.getAttendance();
            List<JSONObject> list = new ArrayList<>();
            if (!new File(FILE).exists()) return list;
            try {
                JSONArray arr = (JSONArray) new JSONParser().parse(readFile(FILE));
                for (Object o : arr) list.add((JSONObject)o);
            } catch (ParseException e) { LOG.log(Level.WARNING,"Attend parse error",e);
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Attend read error",e); }
            DataCache.setAttendance(list);
            return list;
        }
    }

    // ============================================================
    //  FINANCE MANAGER  (event-based master-detail)
    // ============================================================
    static class FinanceManager {
        static final String FILE = "finances.json", BACKUP_FILE = "finances_backup.json";

        // ── Load / Save ──────────────────────────────────────────────────────────

        static List<FinanceTransaction> loadAll() {
            if (DataCache.getFinances() != null) return DataCache.getFinances();
            List<FinanceTransaction> list = new ArrayList<>();
            if (!new File(FILE).exists()) return list;
            try {
                JSONArray arr = (JSONArray) new JSONParser().parse(readFile(FILE));
                for (Object o : arr) {
                    JSONObject j = (JSONObject) o;
                    double amt = 0;
                    Object a = j.get("amount");
                    if (a instanceof Number) amt = ((Number) a).doubleValue();
                    // eventId is read from JSON; older records without it get ""
                    list.add(new FinanceTransaction(
                        s(j,"txId"), s(j,"eventId"), s(j,"date"),
                        s(j,"description"), s(j,"type"), amt));
                }
            } catch (ParseException e) { LOG.log(Level.WARNING,"Finance parse error",e);
            } catch (IOException e)    { LOG.log(Level.SEVERE, "Finance read error",e); }
            list.sort(Comparator.comparing(t -> t.date));
            DataCache.setFinances(list);
            return list;
        }

        @SuppressWarnings("unchecked")
        static void saveAll(List<FinanceTransaction> list) {
            JSONArray arr = new JSONArray();
            for (FinanceTransaction t : list) {
                JSONObject j = new JSONObject();
                j.put("txId",        t.txId);
                j.put("eventId",     t.eventId);
                j.put("date",        t.date);
                j.put("description", t.description);
                j.put("type",        t.type);
                j.put("amount",      t.amount);
                arr.add(j);
            }
            String c = arr.toJSONString();
            try {
                new JSONParser().parse(c);
                writeFile(FILE, c);
                writeFile(BACKUP_FILE, c);
                DataCache.invalidateFinances();
            } catch (ParseException e) { LOG.log(Level.SEVERE,"Bad finance JSON",e); showSaveError("finance");
            } catch (IOException e)    { LOG.log(Level.SEVERE,"Finance write error",e); showSaveError("finance"); }
        }

        // ── CRUD ─────────────────────────────────────────────────────────────────

        static void add(FinanceTransaction t) {
            List<FinanceTransaction> l = new ArrayList<>(loadAll());
            l.add(t);
            l.sort(Comparator.comparing(tx -> tx.date));
            saveAll(l);
        }

        static void delete(String txId) {
            List<FinanceTransaction> l = new ArrayList<>(loadAll());
            l.removeIf(t -> t.txId.equals(txId));
            saveAll(l);
        }

        static String generateId() { return "TX-" + System.currentTimeMillis(); }

        // ── Event-scoped helpers ──────────────────────────────────────────────────

        /** All INCOME transactions for one event (the event's budget entries). */
        static List<FinanceTransaction> getBudgetTransactions(String eventId) {
            List<FinanceTransaction> r = new ArrayList<>();
            for (FinanceTransaction t : loadAll())
                if (TransactionType.INCOME.getLabel().equals(t.type) && eventId.equals(t.eventId))
                    r.add(t);
            return r;
        }

        /** All EXPENSE transactions for one event. */
        static List<FinanceTransaction> getExpensesForEvent(String eventId) {
            List<FinanceTransaction> r = new ArrayList<>();
            for (FinanceTransaction t : loadAll())
                if (TransactionType.EXPENSE.getLabel().equals(t.type) && eventId.equals(t.eventId))
                    r.add(t);
            return r;
        }

        /** Total budget (sum of all INCOME) for one event. */
        static double getEventBudget(String eventId) {
            double s = 0;
            for (FinanceTransaction t : getBudgetTransactions(eventId)) s += t.amount;
            return s;
        }

        /** Total spent (sum of all EXPENSE) for one event. */
        static double getEventSpent(String eventId) {
            double s = 0;
            for (FinanceTransaction t : getExpensesForEvent(eventId)) s += t.amount;
            return s;
        }

        /** Cascade delete: remove all finance records that belong to an event. */
        static void deleteByEvent(String eventId) {
            List<FinanceTransaction> l = new ArrayList<>(loadAll());
            l.removeIf(t -> eventId.equals(t.eventId));
            saveAll(l);
        }

        // ── Global aggregates (kept for backward compatibility / dashboard use) ───

        static double totalIncome()   {
            double s=0; for(FinanceTransaction t:loadAll())
                if(TransactionType.INCOME.getLabel().equals(t.type))  s+=t.amount; return s;
        }
        static double totalExpenses() {
            double s=0; for(FinanceTransaction t:loadAll())
                if(TransactionType.EXPENSE.getLabel().equals(t.type)) s+=t.amount; return s;
        }
        static double netBalance()    { return totalIncome()-totalExpenses(); }
    }

    // ============================================================
    //  BACKUP MANAGER
    // ============================================================
    static class BackupManager {
        static final String BACKUP_DIR="backups", BACKUP_PREFIX="backup_", TIMESTAMP_FMT="yyyy-MM-dd_HH-mm-ss";
        static final String[] TRACKED_FILES = {"members.json","events.json","attendance.json","finances.json","members_backup.json","events_backup.json","attendance_backup.json","finances_backup.json"};
        static void createBootBackup() {
            boolean any=false; for(String f:TRACKED_FILES) if(new File(f).exists()){any=true;break;} if(!any) return;
            File dir=new File(BACKUP_DIR); if(!dir.exists()&&!dir.mkdirs()) return;
            String ts=new SimpleDateFormat(TIMESTAMP_FMT).format(new Date()); File zip=new File(dir,BACKUP_PREFIX+ts+".zip");
            try(java.util.zip.ZipOutputStream zos=new java.util.zip.ZipOutputStream(new java.io.BufferedOutputStream(new FileOutputStream(zip)))){
                for(String fn:TRACKED_FILES){File f=new File(fn);if(!f.exists())continue;zos.putNextEntry(new java.util.zip.ZipEntry(f.getName()));try(FileInputStream fis=new FileInputStream(f)){byte[] buf=new byte[4096];int len;while((len=fis.read(buf))>0)zos.write(buf,0,len);}zos.closeEntry();}
            }catch(IOException e){LOG.log(Level.SEVERE,"Boot backup failed",e);}
            pruneOldBackups(dir,30);
        }
        static void pruneOldBackups(File dir, int keep) {
            File[] zips=dir.listFiles(f->f.isFile()&&f.getName().startsWith(BACKUP_PREFIX)&&f.getName().endsWith(".zip")); if(zips==null||zips.length<=keep) return;
            Arrays.sort(zips,Comparator.comparing(File::getName)); for(int i=0;i<zips.length-keep;i++) zips[i].delete();
        }
        static void restoreBackup(File zip) throws IOException {
            try(java.util.zip.ZipInputStream zis=new java.util.zip.ZipInputStream(new java.io.BufferedInputStream(new FileInputStream(zip)))){
                java.util.zip.ZipEntry e; while((e=zis.getNextEntry())!=null){try(java.io.BufferedOutputStream bos=new java.io.BufferedOutputStream(new FileOutputStream(new File(e.getName())))){byte[] buf=new byte[4096];int len;while((len=zis.read(buf))>0)bos.write(buf,0,len);}zis.closeEntry();}
            } DataCache.invalidateAll();
        }
        static File[] listBackups() {
            File dir=new File(BACKUP_DIR); if(!dir.exists()) return new File[0];
            File[] zips=dir.listFiles(f->f.isFile()&&f.getName().startsWith(BACKUP_PREFIX)&&f.getName().endsWith(".zip")); if(zips==null) return new File[0];
            Arrays.sort(zips,Comparator.comparing(File::getName).reversed()); return zips;
        }
        static String formatBackupLabel(File f) {
            String token=f.getName().replace(BACKUP_PREFIX,"").replace(".zip","");
            try{Date d=new SimpleDateFormat(TIMESTAMP_FMT).parse(token);return new SimpleDateFormat("MMMM dd, yyyy — hh:mm:ss a").format(d);}catch(Exception e){return f.getName();}
        }
    }

    // ============================================================
    //  LOGIN FRAME
    // ============================================================
    static class LoginFrame extends JFrame {

        JTextField     usernameField;
        JPasswordField passwordField;
        JButton        showPassBtn;
        boolean        passwordVisible = false;

        private Image backgroundImage;
        private Image logoImage;

        LoginFrame() {
            setTitle("Student Organization Management System — Sign In");
            setSize(1024, 768);
            setMinimumSize(new Dimension(720, 540));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            try {
                backgroundImage = ImageIO.read(new File(
                    "/Users/calebadrieltingson/Downloads/" +
                    "480851164_624949790137788_3593001177501279188_n.jpg"));
            } catch (Exception e) {
                LOG.log(Level.WARNING, "LoginFrame: could not load background image", e);
            }

            try {
                logoImage = ImageIO.read(new File(
                    "/Users/calebadrieltingson/Downloads/gdgLogo.png"));
            } catch (Exception e) {
                LOG.log(Level.WARNING, "LoginFrame: could not load logo image", e);
            }

            buildUI();
        }

        void buildUI() {
            JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        int pw = getWidth(), ph = getHeight();
                        double scaleX = (double) pw / backgroundImage.getWidth(null);
                        double scaleY = (double) ph / backgroundImage.getHeight(null);
                        double scale  = Math.max(scaleX, scaleY);
                        int dw = (int) Math.ceil(backgroundImage.getWidth(null)  * scale);
                        int dh = (int) Math.ceil(backgroundImage.getHeight(null) * scale);
                        int ox = (pw - dw) / 2;
                        int oy = (ph - dh) / 2;
                        Graphics2D gi = (Graphics2D) g.create();
                        gi.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        gi.drawImage(backgroundImage, ox, oy, dw, dh, null);
                        gi.dispose();
                    } else {
                        g.setColor(new Color(30, 41, 59));
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0, 0, 0, 25));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            backgroundPanel.setOpaque(true);

            UITheme.RoundedPanel card = new UITheme.RoundedPanel(
                UITheme.R_CARD, UITheme.SURFACE, null);
            card.setPreferredSize(new Dimension(420, 540));
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

            JLabel logoLabel = new JLabel();
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            if (logoImage != null) {
                Image scaled = logoImage.getScaledInstance(-1, 48, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
            } else {
                logoLabel = new JLabel() {
                    @Override protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);
                        int cx = getWidth()/2, cy = getHeight()/2, r = 13, gap = 17;
                        g2.setStroke(new BasicStroke(4.5f,
                            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.setColor(new Color(26, 115, 232));
                        g2.drawLine(cx-gap, cy-r, cx-gap-r, cy);
                        g2.drawLine(cx-gap-r, cy, cx-gap, cy+r);
                        g2.setColor(new Color(234, 67, 53));
                        g2.drawLine(cx+gap, cy-r, cx+gap+r, cy);
                        g2.drawLine(cx+gap+r, cy, cx+gap, cy+r);
                        g2.dispose();
                    }
                };
                logoLabel.setPreferredSize(new Dimension(80, 50));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            }

            JLabel heading = new JLabel("Sign in", SwingConstants.CENTER);
            heading.setFont(new Font(UITheme.BASE_FONT, Font.PLAIN, 26));
            heading.setForeground(UITheme.TEXT_PRIMARY);
            heading.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtext = new JLabel(
                "Student Organization Management System", SwingConstants.CENTER);
            subtext.setFont(UITheme.FONT_BODY_BOLD);
            subtext.setForeground(UITheme.TEXT_SECONDARY);
            subtext.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setOpaque(false);
            formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel userLabel = new JLabel("Username");
            userLabel.setFont(UITheme.FONT_BODY_BOLD);
            userLabel.setForeground(UITheme.TEXT_SECONDARY);

            usernameField = new JTextField();
            usernameField.setFont(UITheme.FONT_BODY);
            usernameField.setBackground(UITheme.SURFACE);
            usernameField.setForeground(UITheme.TEXT_PRIMARY);
            usernameField.setOpaque(true);
            usernameField.setBorder(BorderFactory.createCompoundBorder(
                new UITheme.RoundedBorder(UITheme.BORDER, UITheme.R_INPUT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

            JLabel passLabel = new JLabel("Password");
            passLabel.setFont(UITheme.FONT_BODY_BOLD);
            passLabel.setForeground(UITheme.TEXT_SECONDARY);

            passwordField = UITheme.styledPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));

            showPassBtn = new JButton(UITheme.eyeIcon(false));
            showPassBtn.setToolTipText("Show password");
            showPassBtn.setBorderPainted(false);
            showPassBtn.setContentAreaFilled(false);
            showPassBtn.setFocusPainted(false);
            showPassBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            showPassBtn.setPreferredSize(new Dimension(38, 38));
            showPassBtn.addActionListener(e -> togglePasswordVisibility());

            JPanel passRow = new JPanel(new BorderLayout(0, 0));
            passRow.setBackground(UITheme.SURFACE);
            passRow.setOpaque(true);
            passRow.setBorder(new UITheme.RoundedBorder(UITheme.BORDER, UITheme.R_INPUT, 1));
            passRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            passRow.add(passwordField, BorderLayout.CENTER);
            passRow.add(showPassBtn,   BorderLayout.EAST);

            JLabel hint = new JLabel("Default: admin / admin123");
            hint.setFont(UITheme.FONT_CAPTION);
            hint.setForeground(UITheme.TEXT_DISABLED);

            JButton loginBtn = UITheme.primaryBtn("Sign In");
            loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginBtn.setFont(new Font(UITheme.BASE_FONT, Font.BOLD, 14));

            JButton clearBtn = UITheme.ghostBtn("Clear fields");
            clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            clearBtn.setForeground(UITheme.GOOGLE_BLUE);

            JLabel footer = new JLabel(
                "\u00A9 Student Org System v9.0", SwingConstants.CENTER);
            footer.setFont(UITheme.FONT_CAPTION);
            footer.setForeground(UITheme.TEXT_DISABLED);
            footer.setAlignmentX(Component.CENTER_ALIGNMENT);

            java.awt.event.FocusListener focusBorder = new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent e) {
                    ((JComponent) e.getSource()).setBorder(
                        BorderFactory.createCompoundBorder(
                            new UITheme.RoundedBorder(UITheme.GOOGLE_BLUE, UITheme.R_INPUT, 2),
                            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
                }
                public void focusLost(java.awt.event.FocusEvent e) {
                    ((JComponent) e.getSource()).setBorder(
                        BorderFactory.createCompoundBorder(
                            new UITheme.RoundedBorder(UITheme.BORDER, UITheme.R_INPUT, 1),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            };
            usernameField.addFocusListener(focusBorder);

            loginBtn.addActionListener(e -> handleLogin());
            clearBtn.addActionListener(e -> {
                usernameField.setText("");
                passwordField.setText("");
                usernameField.requestFocus();
            });
            passwordField.addActionListener(e -> handleLogin());
            usernameField.addActionListener(e -> passwordField.requestFocus());

            formPanel.add(leftAlign(userLabel));
            formPanel.add(Box.createVerticalStrut(6));
            formPanel.add(usernameField);
            formPanel.add(Box.createVerticalStrut(14));
            formPanel.add(leftAlign(passLabel));
            formPanel.add(Box.createVerticalStrut(6));
            formPanel.add(passRow);
            formPanel.add(Box.createVerticalStrut(6));
            formPanel.add(leftAlign(hint));
            formPanel.add(Box.createVerticalStrut(20));
            formPanel.add(loginBtn);
            formPanel.add(Box.createVerticalStrut(8));
            formPanel.add(clearBtn);

            card.add(logoLabel);
            card.add(Box.createVerticalStrut(16));
            card.add(heading);
            card.add(Box.createVerticalStrut(4));
            card.add(subtext);
            card.add(Box.createVerticalStrut(28));
            card.add(formPanel);
            card.add(Box.createVerticalGlue());
            card.add(footer);

            backgroundPanel.add(card);
            setContentPane(backgroundPanel);
        }

        private JPanel leftAlign(Component c) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            p.setOpaque(false);
            p.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(c);
            return p;
        }

        void togglePasswordVisibility() {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordField.setEchoChar((char) 0);
                showPassBtn.setIcon(UITheme.eyeIcon(true));
                showPassBtn.setToolTipText("Hide password");
            } else {
                passwordField.setEchoChar('\u2022');
                showPassBtn.setIcon(UITheme.eyeIcon(false));
                showPassBtn.setToolTipText("Show password");
            }
        }

        void handleLogin() {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());
            if (user.equals(ADMIN_USERNAME) && PasswordUtils.verify(pass, ADMIN_PASSWORD_HASH)) {
                new MainFrame().setVisible(true);
                dispose();
            } else {
                LOG.log(Level.WARNING, "Failed login: {0}", user);
                JOptionPane.showMessageDialog(this,
                    "<html><b>Incorrect username or password.</b><br>Please try again.</html>",
                    "Sign In Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocus();
            }
        }
    }

    // ============================================================================================
//  PATCH: RESTRUCTURE GUI FOR 5 DISTINCT JFrames
//  File: Studentorgsystem.java
//
//  WHAT CHANGED:
//    JFrame #1 — LoginFrame        (unchanged)
//    JFrame #2 — MainFrame         (MODIFIED — see replacement below)
//    JFrame #3 — MembersFrame      (NEW)
//    JFrame #4 — EventsFrame       (NEW)
//    JFrame #5 — FinancesFrame     (NEW)
//
//  HOW TO APPLY:
//    1. In Studentorgsystem.java, FIND the entire `MainFrame` class (lines ~1419–1501)
//       and REPLACE it with the "REPLACEMENT: MainFrame" block below.
//    2. Immediately AFTER the closing brace of the new MainFrame class,
//       INSERT the three new JFrame classes: MembersFrame, EventsFrame, FinancesFrame.
//    3. All existing panel classes (MembersPanel, EventsPanel, FinancesPanel, etc.),
//       all JDialog classes, and all data-manager classes remain UNCHANGED.
// ============================================================================================


// ============================================================
//  REPLACEMENT: MainFrame  (replaces the old MainFrame class)
//  — Keeps:    Dashboard, Leaderboard, Certificates, Calendar tabs
//  — Removes:  Members, Events, Finances tabs (now separate JFrames)
//  — Adds:     Launcher cards that open the new JFrames
// ============================================================
static class MainFrame extends JFrame {

    // Track the three sub-frames so we can bring them to front instead of
    // opening duplicates when the user clicks the launcher cards again.
    private MembersFrame  membersFrame;
    private EventsFrame   eventsFrame;
    private FinancesFrame financesFrame;

    MainFrame() {
        setTitle("Student Organization Management System v9.0");
        setSize(1120, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(UITheme.BG);
        buildUI();
    }

    void buildUI() {
        // ── Menu bar ──────────────────────────────────────────────────────────
        JMenuBar mb = new JMenuBar();
        mb.setBackground(UITheme.SURFACE);
        mb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER));

        JMenu app = new JMenu("App");
        app.setFont(UITheme.FONT_BODY_BOLD);
        app.setForeground(UITheme.TEXT_PRIMARY);

        JMenuItem logout  = new JMenuItem("Logout");
        JMenuItem exit    = new JMenuItem("Exit");
        JMenuItem export  = new JMenuItem("Export Members to CSV");
        JMenuItem restore = new JMenuItem("Restore Backup (Time Machine)...");

        for (JMenuItem item : new JMenuItem[]{logout, exit, export, restore}) {
            item.setFont(UITheme.FONT_BODY);
            item.setBackground(UITheme.SURFACE);
        }

        logout.addActionListener(e -> {
            // Close all child frames before returning to login
            if (membersFrame  != null) membersFrame.dispose();
            if (eventsFrame   != null) eventsFrame.dispose();
            if (financesFrame != null) financesFrame.dispose();
            new LoginFrame().setVisible(true);
            dispose();
        });
        exit.addActionListener(e -> System.exit(0));
        export.addActionListener(e -> MemberManager.exportCSV());
        restore.addActionListener(e -> new RestoreBackupDialog(this).setVisible(true));

        app.add(export); app.addSeparator(); app.add(restore);
        app.addSeparator(); app.add(logout); app.add(exit);
        mb.add(app);

        // ── Open-Window shortcuts in the menu bar ─────────────────────────────
        JMenu windows = new JMenu("Open");
        windows.setFont(UITheme.FONT_BODY_BOLD);
        windows.setForeground(UITheme.TEXT_PRIMARY);

        JMenuItem miMembers  = new JMenuItem("Members Manager");
        JMenuItem miEvents   = new JMenuItem("Events Manager");
        JMenuItem miFinances = new JMenuItem("Finances Manager");
        for (JMenuItem mi : new JMenuItem[]{miMembers, miEvents, miFinances})
            mi.setFont(UITheme.FONT_BODY);

        miMembers .addActionListener(e -> openMembersFrame());
        miEvents  .addActionListener(e -> openEventsFrame());
        miFinances.addActionListener(e -> openFinancesFrame());
        windows.add(miMembers); windows.add(miEvents); windows.add(miFinances);
        mb.add(windows);

        JLabel appTitle = new JLabel("  Student Org System");
        appTitle.setFont(UITheme.FONT_BODY_BOLD);
        appTitle.setForeground(UITheme.TEXT_SECONDARY);
        mb.add(appTitle);
        setJMenuBar(mb);

        // ── Core panels ───────────────────────────────────────────────────────
        DashboardPanel    dashPanel  = new DashboardPanel();
        LeaderboardPanel  lbPanel    = new LeaderboardPanel();
        CertificatesPanel certPanel  = new CertificatesPanel();
        CalendarPanel     calPanel   = new CalendarPanel();

        // ── Tabbed pane (Dashboard, Leaderboard, Certificates, Calendar) ──────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(UITheme.FONT_BODY_BOLD);
        tabs.setBackground(UITheme.SURFACE);
        tabs.setForeground(UITheme.TEXT_PRIMARY);

        tabs.addTab("Dashboard",    dashPanel);
        tabs.addTab("Leaderboard",  lbPanel);
        tabs.addTab("Certificates", certPanel);
        tabs.addTab("Calendar",     calPanel);

        tabs.addChangeListener(e -> {
            Component sel = tabs.getSelectedComponent();
            if (sel == calPanel)  calPanel.refresh();
            if (sel == dashPanel) dashPanel.refresh();
            if (sel == lbPanel)   lbPanel.refresh();
        });

        // ── Launcher panel (shown in the CENTER above tabs) ───────────────────
        JPanel launcherPanel = buildLauncherPanel();

        // ── Root layout ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG);
        root.add(launcherPanel, BorderLayout.NORTH);
        root.add(tabs,          BorderLayout.CENTER);
        setContentPane(root);

        // ── Auto-refresh timer ────────────────────────────────────────────────
        new javax.swing.Timer(60_000, e -> {
            EventManager.refreshAutoStatuses();
            if (tabs.getSelectedComponent() == dashPanel) dashPanel.refresh();
        }).start();
    }

    /**
     * Builds the horizontal row of launcher cards at the top of the MainFrame.
     * Each card opens one of the three standalone JFrames.
     */
    JPanel buildLauncherPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 14, 4, 14));

        JLabel heading = new JLabel("Module Launcher");
        heading.setFont(UITheme.FONT_SUBHEADING);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));

        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setBackground(UITheme.BG);

        cards.add(buildModuleCard(
            " Members Manager",
            "",
            UITheme.GOOGLE_BLUE,
            UITheme.BLUE_LIGHT,
            "",
            e -> openMembersFrame()));

        cards.add(buildModuleCard(
            "Events Manager",
            "",
            UITheme.GOOGLE_RED,
            UITheme.RED_LIGHT,
            "",
            e -> openEventsFrame()));

        cards.add(buildModuleCard(
            "Finances Manager",
            "",
            UITheme.GOOGLE_GREEN,
            UITheme.GREEN_LIGHT,
            "",
            e -> openFinancesFrame()));

        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(cards,   BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Builds a single styled launcher card.
     *
     * @param title      Module name shown in bold
     * @param subtitle   Short description (use \n for line break)
     * @param accent     Accent colour for the icon pill and button
     * @param bg         Light background colour for the card
     * @param icon       Emoji or short label shown in the icon pill
     * @param action     ActionListener invoked when the "Open" button is clicked
     */
    JPanel buildModuleCard(String title, String subtitle,
                           Color accent, Color bg,
                           String icon,
                           java.awt.event.ActionListener action) {

        UITheme.RoundedPanel card = new UITheme.RoundedPanel(
            new BorderLayout(0, 10), UITheme.R_CARD, bg, UITheme.BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Icon pill ─────────────────────────────────────────────────────────
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font(UITheme.BASE_FONT, Font.PLAIN, 28));
        iconLabel.setPreferredSize(new Dimension(52, 52));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(accent);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Round the icon pill via a sub-panel
        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconWrap.setOpaque(false);
        UITheme.RoundedPanel iconPill = new UITheme.RoundedPanel(
            new BorderLayout(), 14, accent, null);
        iconPill.setPreferredSize(new Dimension(52, 52));
        iconPill.add(iconLabel, BorderLayout.CENTER);
        iconWrap.add(iconPill);

        // ── Text block ────────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SUBHEADING);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        // Convert \n to <br> for HTML rendering
        String htmlSub = "<html>" + subtitle.replace("\n", "<br>") + "</html>";
        JLabel subLabel = new JLabel(htmlSub);
        subLabel.setFont(UITheme.FONT_CAPTION);
        subLabel.setForeground(UITheme.TEXT_SECONDARY);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(4));
        text.add(subLabel);

        // ── Open button ───────────────────────────────────────────────────────
        UITheme.RoundedButton openBtn = new UITheme.RoundedButton(
            "Open →", accent, accent.darker(), accent.darker().darker(), Color.WHITE);
        openBtn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        openBtn.addActionListener(action);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(openBtn);

        if (!icon.isEmpty()) {
    card.add(iconWrap, BorderLayout.WEST);
}
        card.add(text,     BorderLayout.CENTER);
        card.add(btnRow,   BorderLayout.SOUTH);

        // Clicking anywhere on the card also triggers the open action
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { action.actionPerformed(null); }
        });

        return card;
    }

    // ── Frame launchers (open once; bring to front on subsequent clicks) ──────

    void openMembersFrame() {
        if (membersFrame == null || !membersFrame.isDisplayable()) {
            membersFrame = new MembersFrame(this);
            membersFrame.setVisible(true);
        } else {
            membersFrame.toFront();
            membersFrame.requestFocus();
        }
    }

    void openEventsFrame() {
        if (eventsFrame == null || !eventsFrame.isDisplayable()) {
            eventsFrame = new EventsFrame(this);
            eventsFrame.setVisible(true);
        } else {
            eventsFrame.toFront();
            eventsFrame.requestFocus();
        }
    }

    void openFinancesFrame() {
        if (financesFrame == null || !financesFrame.isDisplayable()) {
            financesFrame = new FinancesFrame(this);
            financesFrame.setVisible(true);
        } else {
            financesFrame.toFront();
            financesFrame.requestFocus();
        }
    }
}


// ============================================================
//  JFrame #3 — MEMBERS FRAME  (NEW)
//  Wraps the existing MembersPanel in its own window.
//  All CRUD dialogs (MemberFormDialog) still work because
//  MembersPanel.openAddDialog() uses SwingUtilities.getWindowAncestor(this),
//  which will now correctly resolve to this MembersFrame.
// ============================================================
static class MembersFrame extends JFrame {

    MembersFrame(JFrame owner) {
        setTitle("Members Manager — Student Org System");
        setSize(1080, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // closing this does NOT exit the app
        setLocationRelativeTo(owner);
        buildUI();
    }

    void buildUI() {
        // ── Menu bar ──────────────────────────────────────────────────────────
        JMenuBar mb = new JMenuBar();
        mb.setBackground(UITheme.SURFACE);
        mb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER));

        JMenu menu = new JMenu("Members");
        menu.setFont(UITheme.FONT_BODY_BOLD);
        menu.setForeground(UITheme.TEXT_PRIMARY);

        JMenuItem exportCsv = new JMenuItem("Export to CSV");
        JMenuItem closeWin  = new JMenuItem("Close Window");
        exportCsv.setFont(UITheme.FONT_BODY);
        closeWin .setFont(UITheme.FONT_BODY);

        exportCsv.addActionListener(e -> MemberManager.exportCSV());
        closeWin .addActionListener(e -> dispose());

        menu.add(exportCsv); menu.addSeparator(); menu.add(closeWin);
        mb.add(menu);

        JLabel title = new JLabel("  Members Manager");
        title.setFont(new Font(UITheme.BASE_FONT, Font.BOLD, 13)); 
        title.setForeground(UITheme.TEXT_SECONDARY);  
        mb.add(title);
        setJMenuBar(mb);

        // ── Content ───────────────────────────────────────────────────────────
        MembersPanel panel = new MembersPanel();
        setContentPane(panel);
    }
}


// ============================================================
//  JFrame #4 — EVENTS FRAME  (NEW)
//  Wraps the existing EventsPanel in its own window.
//  All CRUD dialogs (EventFormDialog, CheckInDialog) still work
//  because EventsPanel uses SwingUtilities.getWindowAncestor(this).
// ============================================================
static class EventsFrame extends JFrame {

    EventsFrame(JFrame owner) {
        setTitle("Events Manager — Student Org System");
        setSize(1080, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);
        buildUI();
    }

    void buildUI() {
        // ── Menu bar ──────────────────────────────────────────────────────────
        JMenuBar mb = new JMenuBar();
        mb.setBackground(UITheme.SURFACE);
        mb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER));

        JMenu menu = new JMenu("Events");
        menu.setFont(UITheme.FONT_BODY_BOLD);
        menu.setForeground(UITheme.TEXT_PRIMARY);

        JMenuItem closeWin = new JMenuItem("Close Window");
        closeWin.setFont(UITheme.FONT_BODY);
        closeWin.addActionListener(e -> dispose());

        menu.add(closeWin);
        mb.add(menu);

        JLabel title = new JLabel("  Events Manager");
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_SECONDARY);
        mb.add(title);
        setJMenuBar(mb);

        // ── Content ───────────────────────────────────────────────────────────
        // EventsPanel auto-refreshes status every 60 s via EventManager.refreshAutoStatuses().
        // We add a local timer here so this window stays in sync when it's visible.
        EventsPanel panel = new EventsPanel();
        setContentPane(panel);

        new javax.swing.Timer(60_000, e -> {
            if (isDisplayable()) {
                EventManager.refreshAutoStatuses();
                panel.loadTable();
            }
        }).start();
    }
}


// ============================================================
//  JFrame #5 — FINANCES FRAME  (NEW)
//  Wraps the existing FinancesPanel in its own window.
//  Budget/Expense add dialogs (AddBudgetDialog, AddExpenseDialog)
//  still work because FinancesPanel uses SwingUtilities.getWindowAncestor(this).
// ============================================================
static class FinancesFrame extends JFrame {

    FinancesFrame(JFrame owner) {
        setTitle("Finances Manager — Student Org System");
        setSize(1080, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);
        buildUI();
    }

    void buildUI() {
        // ── Menu bar ──────────────────────────────────────────────────────────
        JMenuBar mb = new JMenuBar();
        mb.setBackground(UITheme.SURFACE);
        mb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER));

        JMenu menu = new JMenu("Finances");
        menu.setFont(UITheme.FONT_BODY_BOLD);
        menu.setForeground(UITheme.TEXT_PRIMARY);

        JMenuItem closeWin = new JMenuItem("Close Window");
        closeWin.setFont(UITheme.FONT_BODY);
        closeWin.addActionListener(e -> dispose());

        menu.add(closeWin);
        mb.add(menu);

       JLabel title = new JLabel("  Finances Manager");
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_SECONDARY);
        mb.add(title);
        setJMenuBar(mb);

        // ── Content ───────────────────────────────────────────────────────────
        FinancesPanel panel = new FinancesPanel();
        setContentPane(panel);
    }
}


// ============================================================================================
//  END OF PATCH — No other classes need to be changed.
//
//  SUMMARY OF ALL 5 JFrames AFTER APPLYING THIS PATCH:
//
//    JFrame #1  LoginFrame     — sign-in screen (unchanged)
//    JFrame #2  MainFrame      — dashboard hub with launcher cards + Dashboard /
//                                Leaderboard / Certificates / Calendar tabs
//    JFrame #3  MembersFrame   — full Members CRUD, attendance sheet, search
//    JFrame #4  EventsFrame    — full Events CRUD, Check-In, status filters
//    JFrame #5  FinancesFrame  — per-event budget & expense tracking
//
//  Supporting JDialogs (unchanged, still launched from within their respective frames):
//    • MemberFormDialog     (Add / Edit member)
//    • EventFormDialog      (Create / Edit event)
//    • AddBudgetDialog      (Add budget to event)
//    • AddExpenseDialog     (Add expense to event)
//    • CheckInDialog        (Attendance check-in)
//    • CalendarPickerDialog (Date picker)
//    • RestoreBackupDialog  (Backup restore)
//
//  All JSON persistence, error handling, input validation, caching,
//  and Material Design styling are completely untouched.
// ============================================================================================
    // ============================================================
    //  DASHBOARD
    // ============================================================
    static class DashboardPanel extends JPanel {
        KpiCard totalCard, activeCard, eventsCard, avgCard;
        DonutChartPanel statusChart;
        BarChartPanel   courseChart;
        DefaultTableModel top5Model;

        DashboardPanel() {
            setLayout(new BorderLayout(12, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            setBackground(UITheme.BG);
            buildUI();
            refresh();
        }

        void buildUI() {
            JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
            kpiRow.setOpaque(false);
            totalCard  = new KpiCard("Total Members",  "Members",   UITheme.GOOGLE_BLUE);
            activeCard = new KpiCard("Active Members",  "Active",    UITheme.GOOGLE_GREEN);
            eventsCard = new KpiCard("Total Events",    "Events",    UITheme.ORANGE);
            avgCard    = new KpiCard("Avg Attendance",  "Attendance", new Color(103, 58, 183));
            kpiRow.add(totalCard); kpiRow.add(activeCard); kpiRow.add(eventsCard); kpiRow.add(avgCard);

            statusChart = new DonutChartPanel("Member Status Distribution");
            courseChart = new BarChartPanel("Members per Course");

            top5Model = new DefaultTableModel(new String[]{"#", "Name", "Attended", "Points"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable top5Table = new JTable(top5Model);
            UITheme.styleTable(top5Table);

            JPanel top5Wrap = new JPanel(new BorderLayout(4, 8));
            top5Wrap.setBackground(UITheme.SURFACE);
            top5Wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
            JLabel top5Title = new JLabel("Top 5 Most Active Members");
            top5Title.setFont(UITheme.FONT_BODY_BOLD);
            top5Title.setForeground(UITheme.TEXT_PRIMARY);
            top5Wrap.add(top5Title, BorderLayout.NORTH);
            top5Wrap.add(new JScrollPane(top5Table), BorderLayout.CENTER);

            JPanel chartsRow = new JPanel(new GridLayout(1, 3, 12, 0));
            chartsRow.setOpaque(false);
            chartsRow.add(wrapChart(statusChart));
            chartsRow.add(wrapChart(courseChart));
            chartsRow.add(top5Wrap);

            add(kpiRow,    BorderLayout.NORTH);
            add(chartsRow, BorderLayout.CENTER);
        }

        JPanel wrapChart(JPanel chart) {
            UITheme.RoundedPanel wrap = new UITheme.RoundedPanel(UITheme.R_CARD, UITheme.SURFACE, UITheme.BORDER);
            wrap.setLayout(new BorderLayout());
            wrap.add(chart, BorderLayout.CENTER);
            return wrap;
        }

        void refresh() {
            List<Member> members = MemberManager.loadAll();
            List<Event>  events  = EventManager.loadAll();
            int total = members.size(), active = 0;
            for (Member m : members) if (MemberStatus.ACTIVE.getLabel().equals(m.status)) active++;
            totalCard.setValue(String.valueOf(total));
            activeCard.setValue(String.valueOf(active));
            eventsCard.setValue(String.valueOf(events.size()));
            if (total == 0 || events.isEmpty()) { avgCard.setValue("N/A"); }
            else { int sum=0; for(Member m:members) sum+=AttendanceManager.countAttended(m.studentId); avgCard.setValue(String.format("%.0f%%", sum*100.0/(total*events.size()))); }

            Map<String,Integer> statusData = new LinkedHashMap<>();
            for (MemberStatus st : MemberStatus.values()) { int cnt=0; for(Member m:members) if(st.getLabel().equals(m.status)) cnt++; if(cnt>0) statusData.put(st.getLabel(),cnt); }
            statusChart.setData(statusData);

            Map<String,Integer> rawCourse = new LinkedHashMap<>();
            for (Course c : Course.values()) { int cnt=0; for(Member m:members) if(c.name().equals(m.course)) cnt++; if(cnt>0) rawCourse.put(c.name(),cnt); }
            List<Map.Entry<String,Integer>> sorted = new ArrayList<>(rawCourse.entrySet()); sorted.sort((a,b)->b.getValue()-a.getValue());
            Map<String,Integer> courseData = new LinkedHashMap<>(); for(int i=0;i<Math.min(8,sorted.size());i++) courseData.put(sorted.get(i).getKey(),sorted.get(i).getValue());
            courseChart.setData(courseData);

            top5Model.setRowCount(0);
            List<int[]> scores = new ArrayList<>();
            for (int i=0;i<members.size();i++) scores.add(new int[]{i, LeaderboardPanel.calcPoints(members.get(i))});
            scores.sort((a,b)->b[1]-a[1]);
            for (int i=0; i<Math.min(5,scores.size()); i++) {
                Member m = members.get(scores.get(i)[0]);
                top5Model.addRow(new Object[]{"#"+(i+1), m.name, AttendanceManager.countAttended(m.studentId), scores.get(i)[1]+" pts"});
            }
        }

        static class KpiCard extends UITheme.RoundedPanel {
            private final JLabel valLbl;
            KpiCard(String title, String iconText, Color accent) {
                super(UITheme.R_CARD, UITheme.SURFACE, UITheme.BORDER);
                setLayout(new BorderLayout(10, 4));
                setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

                JPanel stripe = new UITheme.RoundedPanel(4, accent);
                stripe.setPreferredSize(new Dimension(5, 0));

                JLabel iconLbl = new JLabel(iconText);
                iconLbl.setFont(new Font(UITheme.BASE_FONT, Font.BOLD, 11));
                iconLbl.setForeground(accent);
                iconLbl.setHorizontalAlignment(SwingConstants.RIGHT);

                valLbl = new JLabel("—");
                valLbl.setFont(new Font(UITheme.BASE_FONT, Font.BOLD, 30));
                valLbl.setForeground(UITheme.TEXT_PRIMARY);

                JLabel titleLbl = new JLabel(title);
                titleLbl.setFont(UITheme.FONT_CAPTION);
                titleLbl.setForeground(UITheme.TEXT_SECONDARY);

                JPanel textArea = new JPanel(new GridLayout(2, 1, 0, 2));
                textArea.setOpaque(false);
                textArea.add(valLbl);
                textArea.add(titleLbl);

                add(stripe, BorderLayout.WEST);
                add(iconLbl, BorderLayout.EAST);
                add(textArea, BorderLayout.CENTER);
            }
            void setValue(String v) { valLbl.setText(v); }
        }

        static class DonutChartPanel extends JPanel {
            private final String title; private Map<String,Integer> data = new LinkedHashMap<>();
            private static final Color[] COLORS = {
                UITheme.GOOGLE_BLUE, UITheme.GOOGLE_GREEN, UITheme.GOOGLE_RED,
                UITheme.GOOGLE_YELLOW, new Color(103,58,183), UITheme.ORANGE
            };
            DonutChartPanel(String t) { this.title=t; setOpaque(false); }
            void setData(Map<String,Integer> d) { data.clear(); data.putAll(d); repaint(); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),UITheme.R_CARD,UITheme.R_CARD);
                int w=getWidth(),h=getHeight();
                g2.setFont(UITheme.FONT_BODY_BOLD); g2.setColor(UITheme.TEXT_PRIMARY); g2.drawString(title,12,26);
                if(data.isEmpty()){g2.setColor(UITheme.TEXT_SECONDARY);g2.setFont(UITheme.FONT_CAPTION);g2.drawString("No data",w/2-24,h/2);g2.dispose();return;}
                int total=0; for(int v:data.values()) total+=v;
                int diam=Math.min(w-130,h-60), cx=diam/2+15, cy=(h+36)/2; double start=90; int ci=0;
                List<Map.Entry<String,Integer>> entries=new ArrayList<>(data.entrySet());
                for(Map.Entry<String,Integer> e:entries){double arc=360.0*e.getValue()/total;g2.setColor(COLORS[ci%COLORS.length]);g2.fill(new Arc2D.Double(cx-diam/2,cy-diam/2,diam,diam,start,arc,Arc2D.PIE));start+=arc;ci++;}
                g2.setColor(UITheme.SURFACE); int inner=diam/2; g2.fillOval(cx-inner/2,cy-inner/2,inner,inner);
                g2.setFont(UITheme.FONT_BODY_BOLD); g2.setColor(UITheme.TEXT_PRIMARY);
                String tot=String.valueOf(total); FontMetrics fm=g2.getFontMetrics(); g2.drawString(tot,cx-fm.stringWidth(tot)/2,cy+6);
                int lx=cx+diam/2+12, ly=cy-diam/2+10; ci=0;
                for(Map.Entry<String,Integer> e:entries){g2.setColor(COLORS[ci%COLORS.length]);g2.fillRoundRect(lx,ly,10,10,5,5);g2.setColor(UITheme.TEXT_PRIMARY);g2.setFont(UITheme.FONT_CAPTION);int pct=(int)Math.round(e.getValue()*100.0/total);g2.drawString(e.getKey()+" ("+pct+"%)",lx+14,ly+10);ly+=18;ci++;}
                g2.dispose();
            }
        }

        static class BarChartPanel extends JPanel {
            private final String title; private Map<String,Integer> data = new LinkedHashMap<>();
            BarChartPanel(String t) { this.title=t; setOpaque(false); }
            void setData(Map<String,Integer> d) { data.clear(); data.putAll(d); repaint(); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),UITheme.R_CARD,UITheme.R_CARD);
                int w=getWidth(),h=getHeight();
                g2.setFont(UITheme.FONT_BODY_BOLD); g2.setColor(UITheme.TEXT_PRIMARY); g2.drawString(title,12,26);
                if(data.isEmpty()){g2.setColor(UITheme.TEXT_SECONDARY);g2.setFont(UITheme.FONT_CAPTION);g2.drawString("No data",w/2-24,h/2);g2.dispose();return;}
                int maxVal=0; for(int v:data.values()) if(v>maxVal) maxVal=v;
                int lp=55,rp=30,tp=36,bp=12,chartW=w-lp-rp,chartH=h-tp-bp,n=data.size(),bh=Math.max(6,chartH/n-5),gap=(chartH-n*bh)/(n+1);
                List<Map.Entry<String,Integer>> entries=new ArrayList<>(data.entrySet());
                Color[] barColors={UITheme.GOOGLE_BLUE,new Color(60,140,210),new Color(50,150,200),
                    new Color(40,160,190),UITheme.GOOGLE_GREEN,UITheme.ORANGE,UITheme.GOOGLE_RED,new Color(103,58,183)};
                for(int i=0;i<entries.size();i++){Map.Entry<String,Integer> e=entries.get(i);int y=tp+gap+i*(bh+gap);int bw=maxVal==0?0:(int)((double)e.getValue()/maxVal*chartW);
                    g2.setColor(barColors[i%barColors.length]);
                    g2.fill(new RoundRectangle2D.Float(lp,y,Math.max(bw,4),bh,6,6));
                    g2.setColor(UITheme.TEXT_PRIMARY);g2.setFont(UITheme.FONT_CAPTION);FontMetrics fm=g2.getFontMetrics();String lbl=e.getKey().length()>7?e.getKey().substring(0,6)+"…":e.getKey();g2.drawString(lbl,lp-fm.stringWidth(lbl)-4,y+bh/2+4);g2.drawString(String.valueOf(e.getValue()),lp+bw+5,y+bh/2+4);}
                g2.dispose();
            }
        }
    }

    // ============================================================
    //  LEADERBOARD
    // ============================================================
    static class LeaderboardPanel extends JPanel {
        DefaultTableModel model;
        LeaderboardPanel() { setLayout(new BorderLayout(8,8)); setBorder(BorderFactory.createEmptyBorder(12,12,12,12)); setBackground(UITheme.BG); buildUI(); refresh(); }
        void buildUI() {
            JPanel header = UITheme.sectionHeader("Member Leaderboard");
            JLabel sub = new JLabel("  Points: 10 per event  ·  Position bonuses  ·  Active status +20");
            sub.setFont(UITheme.FONT_CAPTION); sub.setForeground(new Color(180,200,230));
            JButton refreshBtn = UITheme.primaryBtn("Refresh");
            refreshBtn.addActionListener(e -> refresh());
            header.add(sub, BorderLayout.CENTER); header.add(refreshBtn, BorderLayout.EAST);

            model = new DefaultTableModel(new String[]{"Rank","Medal","Name","Position","Course","Attended","Points"},0){
                public boolean isCellEditable(int r,int c){return false;}
            };
            JTable table = new JTable(model);
            UITheme.styleTable(table);
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(50);
            table.getColumnModel().getColumn(2).setPreferredWidth(200);

            @SuppressWarnings("serial") DefaultTableCellRenderer rend = new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col) {
                    super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                    if (!sel) {
                        if (row==0) { setBackground(new Color(255,248,180)); setForeground(new Color(120,80,0)); setFont(UITheme.FONT_BODY_BOLD); }
                        else if (row==1) { setBackground(new Color(240,240,248)); setForeground(new Color(70,70,90)); setFont(UITheme.FONT_BODY_BOLD); }
                        else if (row==2) { setBackground(new Color(255,238,220)); setForeground(new Color(120,60,20)); setFont(UITheme.FONT_BODY_BOLD); }
                        else { setBackground(row%2==0?UITheme.SURFACE:UITheme.SURFACE_ALT); setForeground(UITheme.TEXT_PRIMARY); setFont(UITheme.FONT_TABLE_BODY); }
                    }
                    setBorder(BorderFactory.createEmptyBorder(0,8,0,8)); return this;
                }
            };
            for (int i=0;i<7;i++) table.getColumnModel().getColumn(i).setCellRenderer(rend);

            JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            legend.setBackground(UITheme.SURFACE);
            legend.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            legend.add(UITheme.chip("1st Place", new Color(255,248,180), new Color(120,80,0)));
            legend.add(UITheme.chip("2nd Place", new Color(240,240,248), new Color(70,70,90)));
            legend.add(UITheme.chip("3rd Place", new Color(255,238,220), new Color(120,60,20)));
            JLabel pts = new JLabel("   +10 pts/event  |  President +50  |  VP +40  |  Sec/Treas/Aud +30  |  Officer +20  |  Active +20");
            pts.setFont(UITheme.FONT_CAPTION); pts.setForeground(UITheme.TEXT_SECONDARY);
            legend.add(pts);

            JPanel tableCard = new JPanel(new BorderLayout());
            tableCard.setBackground(UITheme.SURFACE);
            tableCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
            tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

            add(header,    BorderLayout.NORTH);
            add(tableCard, BorderLayout.CENTER);
            add(legend,    BorderLayout.SOUTH);
        }
        void refresh() {
            model.setRowCount(0);
            List<Member> members = new ArrayList<>(MemberManager.loadAll());
            members.sort((a,b) -> calcPoints(b)-calcPoints(a));
            String[] medals = {"Gold","Silver","Bronze"};
            int totalEvents = EventManager.loadAll().size();
            for (int i=0; i<members.size(); i++) {
                Member m = members.get(i); int att = AttendanceManager.countAttended(m.studentId);
                model.addRow(new Object[]{"#"+(i+1), i<3?medals[i]:"", m.name, m.position, m.course, att+"/"+totalEvents, calcPoints(m)+" pts"});
            }
        }
        static int calcPoints(Member m) {
            int pts = AttendanceManager.countAttended(m.studentId)*10;
            switch(m.position){case"President":pts+=50;break;case"Vice President":pts+=40;break;case"Secretary":case"Treasurer":case"Auditor":pts+=30;break;case"Officer":pts+=20;break;default:break;}
            if (MemberStatus.ACTIVE.getLabel().equals(m.status)) pts+=20; return pts;
        }
    }

    // ============================================================
    //  FINANCES  —  Event-based Master-Detail Budget Tracker
    // ============================================================

    /**
     * FinancesPanel  — master-detail layout
     *
     * ┌──────────────────────────────────────────────────────────────┐
     * │  SUMMARY BAR  (Total Budget | Total Spent | Net Balance)     │
     * ├──────────────────────────────────────────────────────────────┤
     * │  TOOLBAR  [Add Budget to Event]  [Delete Budget Row]         │
     * ├──────────────────────────────────────────────────────────────┤
     * │  MASTER TABLE  (one row per event that has a budget)         │
     * │  Event Name | Date | Budget | Spent | Remaining | Status    │
     * ├──────────────────────────────────────────────────────────────┤
     * │  DETAIL PANEL  (shown when a master row is selected)        │
     * │  Toolbar: [Add Expense]  [Delete Expense]                   │
     * │  Expense table: Date | Description | Amount                 │
     * └──────────────────────────────────────────────────────────────┘
     */
    static class FinancesPanel extends JPanel {

        // ── Summary bar (instance field so refreshSummary can repaint it) ───────
        private JPanel summaryBar;
        // ── Summary bar labels ────────────────────────────────────────────────────
        private JLabel budgetLbl, spentLbl, balLbl;

        // ── Master table ──────────────────────────────────────────────────────────
        private DefaultTableModel masterModel;
        private JTable            masterTable;

        // Master columns (col 6 = hidden eventId)
        private static final String[] MASTER_COLS =
            {"Event Name","Date","Total Budget","Total Spent","Remaining","Status","__eventId"};
        private static final int COL_MASTER_EVENT_ID = 6;

        // ── Detail panel ──────────────────────────────────────────────────────────
        private DefaultTableModel detailModel;
        private JTable            detailTable;
        private JLabel            detailHeader;
        private JButton           addExpBtn, delExpBtn;

        // Detail columns (col 3 = hidden txId)
        private static final String[] DETAIL_COLS =
            {"Date","Description","Amount","__txId"};
        private static final int COL_DETAIL_TX_ID = 3;

        // ── Currently-selected event ──────────────────────────────────────────────
        private String selectedEventId = null;

        // ─────────────────────────────────────────────────────────────────────────

        FinancesPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setBackground(UITheme.BG);
            buildUI();
            refresh();
        }

        // ── UI construction ───────────────────────────────────────────────────────
        //
        //  FIX 1 — LAYOUT: Removed all duplicate intermediate panels (masterCard,
        //  masterWrapper, detailCard, detailWrapper) that caused Swing's single-parent
        //  rule to silently reparent components into SOUTH constraints (size-capped)
        //  instead of CENTER (stretches to fill).  Every component is now added to
        //  exactly ONE parent in ONE place.
        //
        //  Clean hierarchy:
        //
        //  FinancesPanel (BorderLayout)
        //  ├── NORTH  summaryBar
        //  └── CENTER JSplitPane (VERTICAL)
        //      ├── TOP    masterPanel (BorderLayout)
        //      │   ├── NORTH  masterTopArea  (header + toolbar stacked)
        //      │   └── CENTER JScrollPane(masterTable)   ← fills remaining height
        //      └── BOTTOM detailPanel (BorderLayout)
        //          ├── NORTH  detailTopArea  (detailHeader + detailToolbar stacked)
        //          └── CENTER JScrollPane(detailTable)   ← fills remaining height

        void buildUI() {

            // ── Summary bar ───────────────────────────────────────────────────────
            summaryBar = new JPanel(new GridLayout(1, 3, 12, 0));
            summaryBar.setOpaque(false);
            summaryBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            budgetLbl = summCard("Total Budget", UITheme.GOOGLE_BLUE);
            spentLbl  = summCard("Total Spent",  UITheme.GOOGLE_RED);
            balLbl    = summCard("Net Balance",  UITheme.GOOGLE_GREEN);
            summaryBar.add(budgetLbl);
            summaryBar.add(spentLbl);
            summaryBar.add(balLbl);

            // ── Master table setup ────────────────────────────────────────────────
            masterModel = new DefaultTableModel(MASTER_COLS, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            masterTable = new JTable(masterModel);
            UITheme.styleTable(masterTable);
            masterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            hideCol(masterTable, COL_MASTER_EVENT_ID);
            masterTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    if (!sel) {
                        if (col == 4) { // Remaining
                            String txt = v != null ? v.toString() : "";
                            boolean over = txt.startsWith("-") || txt.startsWith("−");
                            setBackground(over ? UITheme.RED_LIGHT : UITheme.GREEN_LIGHT);
                            setForeground(over ? new Color(183,28,28) : new Color(27,94,32));
                            setFont(UITheme.FONT_BODY_BOLD);
                        } else if (col == 5) { // Status
                            String st = v != null ? v.toString() : "";
                            if ("Over Budget".equals(st)) {
                                setBackground(UITheme.RED_LIGHT);    setForeground(new Color(183,28,28)); setFont(UITheme.FONT_BODY_BOLD);
                            } else if ("On Budget".equals(st)) {
                                setBackground(UITheme.YELLOW_LIGHT); setForeground(new Color(130,90,0));  setFont(UITheme.FONT_BODY_BOLD);
                            } else {
                                setBackground(UITheme.GREEN_LIGHT);  setForeground(new Color(27,94,32));  setFont(UITheme.FONT_BODY_BOLD);
                            }
                        } else {
                            setBackground(row%2==0 ? UITheme.SURFACE : UITheme.SURFACE_ALT);
                            setForeground(UITheme.TEXT_PRIMARY); setFont(UITheme.FONT_TABLE_BODY);
                        }
                    }
                    return this;
                }
            });
            masterTable.getColumnModel().getColumn(0).setPreferredWidth(210);
            masterTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            masterTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            masterTable.getColumnModel().getColumn(3).setPreferredWidth(120);
            masterTable.getColumnModel().getColumn(4).setPreferredWidth(120);
            masterTable.getColumnModel().getColumn(5).setPreferredWidth(100);

            // ── Master toolbar ─────────────────────────────────────────────────────
            JPanel masterToolbar = UITheme.toolbarPanel();
            JButton addBudgetBtn = UITheme.successBtn("＋ Add Budget to Event");
            JButton delMasterBtn = UITheme.ghostBtn("Delete Budget Row");
            delMasterBtn.setForeground(UITheme.GOOGLE_RED);
            masterToolbar.add(addBudgetBtn);
            masterToolbar.add(delMasterBtn);

            // ── Master panel: header → toolbar → table (single, clean hierarchy) ──
            JPanel masterTopArea = new JPanel(new BorderLayout());
            masterTopArea.setBackground(UITheme.SURFACE);
            masterTopArea.add(UITheme.sectionHeader("Event Budgets"), BorderLayout.NORTH);
            masterTopArea.add(masterToolbar,                           BorderLayout.SOUTH);

            JScrollPane masterSP = new JScrollPane(masterTable);
            masterSP.setBorder(null);
            masterSP.getViewport().setBackground(UITheme.SURFACE);

            JPanel masterPanel = new JPanel(new BorderLayout(0, 0));
            masterPanel.setBackground(UITheme.SURFACE);
            masterPanel.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
            masterPanel.add(masterTopArea, BorderLayout.NORTH);   // fixed height
            masterPanel.add(masterSP,      BorderLayout.CENTER);  // stretches to fill ✓

            // ── Detail table setup ────────────────────────────────────────────────
            detailHeader = new JLabel("  ▸  Select an event above to view its expenses");
            detailHeader.setFont(UITheme.FONT_BODY_BOLD);
            detailHeader.setForeground(UITheme.TEXT_SECONDARY);
            detailHeader.setOpaque(true);
            detailHeader.setBackground(new Color(248, 249, 250));
            detailHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

            detailModel = new DefaultTableModel(DETAIL_COLS, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            detailTable = new JTable(detailModel);
            UITheme.styleTable(detailTable);
            detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            hideCol(detailTable, COL_DETAIL_TX_ID);
            detailTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    if (!sel) {
                        if (col == 2) { // Amount
                            setBackground(UITheme.RED_LIGHT);
                            setForeground(new Color(183,28,28));
                            setFont(UITheme.FONT_BODY_BOLD);
                        } else {
                            setBackground(row%2==0 ? UITheme.SURFACE : UITheme.SURFACE_ALT);
                            setForeground(UITheme.TEXT_PRIMARY);
                            setFont(UITheme.FONT_TABLE_BODY);
                        }
                    }
                    return this;
                }
            });
            detailTable.getColumnModel().getColumn(0).setPreferredWidth(110);
            detailTable.getColumnModel().getColumn(1).setPreferredWidth(360);
            detailTable.getColumnModel().getColumn(2).setPreferredWidth(130);

            // ── Detail toolbar ────────────────────────────────────────────────────
            addExpBtn = UITheme.dangerBtn("＋ Add Expense");
            delExpBtn = UITheme.ghostBtn("Delete Expense");
            delExpBtn.setForeground(UITheme.GOOGLE_RED);
            addExpBtn.setEnabled(false);
            delExpBtn.setEnabled(false);
            JPanel detailToolbar = UITheme.toolbarPanel();
            detailToolbar.add(addExpBtn);
            detailToolbar.add(delExpBtn);

            // ── Detail panel: header → toolbar → table (single, clean hierarchy) ─
            JPanel detailTopArea = new JPanel(new BorderLayout());
            detailTopArea.setBackground(UITheme.SURFACE);
            detailTopArea.add(detailHeader,  BorderLayout.NORTH);
            detailTopArea.add(detailToolbar, BorderLayout.SOUTH);

            JScrollPane detailSP = new JScrollPane(detailTable);
            detailSP.setBorder(null);
            detailSP.getViewport().setBackground(UITheme.SURFACE);

            JPanel detailPanel = new JPanel(new BorderLayout(0, 0));
            detailPanel.setBackground(UITheme.SURFACE);
            detailPanel.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
            detailPanel.add(detailTopArea, BorderLayout.NORTH);   // fixed height
            detailPanel.add(detailSP,      BorderLayout.CENTER);  // stretches to fill ✓

            // ── Split pane ────────────────────────────────────────────────────────
            JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
            splitPane.setResizeWeight(0.55);
            splitPane.setDividerSize(6);
            splitPane.setBorder(null);
            splitPane.setBackground(UITheme.BG);

            // ── Assemble root ─────────────────────────────────────────────────────
            add(summaryBar, BorderLayout.NORTH);
            add(splitPane,  BorderLayout.CENTER);

            // ── Wire listeners ────────────────────────────────────────────────────

            // Master selection → refresh detail
            masterTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) refreshDetail();
            });

            // FIX 3 — Use Window (not JFrame) so the cast never fails regardless of
            // which Window subclass contains this panel at runtime.
            addBudgetBtn.addActionListener(e -> {
                Window win = SwingUtilities.getWindowAncestor(this);
                new AddBudgetDialog(win).setVisible(true);
                refresh();
            });

            delMasterBtn.addActionListener(e -> {
                int row = masterTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Please select an event budget row to delete.",
                        "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String eid    = (String) masterModel.getValueAt(row, COL_MASTER_EVENT_ID);
                String evName = (String) masterModel.getValueAt(row, 0);
                String warn = "<html>Delete <b>all finance records</b> for:<br><br>"
                    + "&nbsp;&nbsp;<b>" + evName + "</b><br><br>"
                    + "This will remove the budget entry <i>and</i> all linked expenses.<br>"
                    + "This <b>cannot be undone</b>. Proceed?</html>";
                if (JOptionPane.showConfirmDialog(this, new JLabel(warn),
                        "Confirm Delete", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    FinanceManager.deleteByEvent(eid);
                    refresh();
                }
            });

            // FIX 3 — Use Window (not JFrame) for parent; avoids ClassCastException.
            addExpBtn.addActionListener(e -> {
                if (selectedEventId == null) return;
                Window win = SwingUtilities.getWindowAncestor(this);
                new AddExpenseDialog(win, selectedEventId).setVisible(true);
                // Refresh detail first (reloads expense rows), then summary & master row
                refreshDetail();
                refreshSummary();
                refreshMasterRow(selectedEventId);
            });

            delExpBtn.addActionListener(e -> {
                int row = detailTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Please select an expense row to delete.",
                        "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String txId = (String) detailModel.getValueAt(row, COL_DETAIL_TX_ID);
                if (JOptionPane.showConfirmDialog(this, "Delete this expense?",
                        "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    FinanceManager.delete(txId);
                    refreshDetail();
                    refreshSummary();
                    refreshMasterRow(selectedEventId);
                }
            });

            detailTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting())
                    delExpBtn.setEnabled(detailTable.getSelectedRow() >= 0);
            });
        }

        // ── Refresh methods ───────────────────────────────────────────────────────

        /** Full refresh: summary + master table (called on tab switch). */
        void refresh() {
            refreshMaster();
            refreshSummary();
            // Attempt to re-select the previously-selected event
            if (selectedEventId != null) {
                reSelectEvent(selectedEventId);
            } else {
                detailModel.setRowCount(0);
                addExpBtn.setEnabled(false);
                delExpBtn.setEnabled(false);
            }
        }

        /** Rebuild the master table from all events that have at least one finance record. */
        private void refreshMaster() {
            masterModel.setRowCount(0);

            // Gather all eventIds that appear in the finance ledger
            Set<String> seen = new LinkedHashSet<>();
            for (FinanceTransaction t : FinanceManager.loadAll())
                if (!t.eventId.isEmpty()) seen.add(t.eventId);

            for (String eid : seen) {
                Event ev      = EventManager.findById(eid);
                String evName = (ev != null) ? ev.name : "(Deleted Event)";
                String evDate = (ev != null) ? ev.date : "—";
                double budget  = FinanceManager.getEventBudget(eid);
                double spent   = FinanceManager.getEventSpent(eid);
                double remain  = budget - spent;
                String status  = remain < 0 ? "Over Budget"
                               : remain == 0 ? "On Budget"
                               : "Under Budget";
                masterModel.addRow(new Object[]{
                    evName,
                    evDate,
                    String.format("₱%.2f", budget),
                    String.format("₱%.2f", spent),
                    String.format("₱%.2f", remain),
                    status,
                    eid   // hidden
                });
            }
        }

        /** Update only the master row corresponding to eventId (fast path after add/delete expense). */
        private void refreshMasterRow(String eventId) {
            if (eventId == null) return;
            for (int i = 0; i < masterModel.getRowCount(); i++) {
                if (eventId.equals(masterModel.getValueAt(i, COL_MASTER_EVENT_ID))) {
                    double budget = FinanceManager.getEventBudget(eventId);
                    double spent  = FinanceManager.getEventSpent(eventId);
                    double remain = budget - spent;
                    String status = remain < 0 ? "Over Budget"
                                  : remain == 0 ? "On Budget"
                                  : "Under Budget";
                    masterModel.setValueAt(String.format("₱%.2f", budget), i, 2);
                    masterModel.setValueAt(String.format("₱%.2f", spent),  i, 3);
                    masterModel.setValueAt(String.format("₱%.2f", remain), i, 4);
                    masterModel.setValueAt(status, i, 5);
                    break;
                }
            }
        }

        /** Update global summary cards.
         *
         * FIX — ORPHAN-SAFE TOTALS
         * The old code called FinanceManager.totalIncome() / totalExpenses() which
         * blindly sum every record in finances.json, including "orphaned" rows whose
         * eventId no longer matches any existing event (e.g. left over from testing
         * or a previous import).  That caused the summary to show a non-zero value
         * (e.g. ₱3,500.00) even when no events are registered.
         *
         * The fix: build the set of currently-existing event IDs first, then only
         * accumulate amounts for transactions that belong to a real event.  Orphaned
         * rows are silently ignored in the display — they are not deleted, but they
         * will never pollute the totals again.
         *
         * The summaryBar panel is also revalidated so the GridLayout container
         * re-renders its children whenever their HTML text changes.
         */
        private void refreshSummary() {
            // Always read fresh from disk so any add / delete is reflected immediately.
            DataCache.invalidateFinances();

            // Collect the IDs of events that currently exist.
            java.util.Set<String> validIds = new java.util.HashSet<>();
            for (Event ev : EventManager.loadAll()) validIds.add(ev.eventId);

            // Sum only transactions that belong to a real event.
            double totalBudget = 0, totalSpent = 0;
            for (FinanceTransaction t : FinanceManager.loadAll()) {
                if (!validIds.contains(t.eventId)) continue; // skip orphans
                if (TransactionType.INCOME.getLabel().equals(t.type))  totalBudget += t.amount;
                else if (TransactionType.EXPENSE.getLabel().equals(t.type)) totalSpent  += t.amount;
            }
            double net = totalBudget - totalSpent;

            budgetLbl.setText(summHtml("Total Budget", totalBudget));
            spentLbl .setText(summHtml("Total Spent",  totalSpent));
            balLbl   .setText(summHtml("Net Balance",  net));
            balLbl.setBackground(net >= 0 ? UITheme.GOOGLE_GREEN : UITheme.GOOGLE_RED);

            // Revalidate each label AND the parent panel so the fixed-size
            // GridLayout cells are redrawn with the updated HTML content.
            budgetLbl.revalidate(); budgetLbl.repaint();
            spentLbl .revalidate(); spentLbl .repaint();
            balLbl   .revalidate(); balLbl   .repaint();
            summaryBar.revalidate();
            summaryBar.repaint();
        }

        private String summHtml(String title, double val) {
            return "<html><center><small>" + title + "</small><br><b>₱"
                + String.format("%.2f", val) + "</b></center></html>";
        }

        /** Refresh the detail table for the currently-selected event. */
        private void refreshDetail() {
            int row = masterTable.getSelectedRow();
            if (row < 0) {
                selectedEventId = null;
                detailModel.setRowCount(0);
                detailHeader.setText("  Select an event above to view expenses");
                addExpBtn.setEnabled(false);
                delExpBtn.setEnabled(false);
                return;
            }

            selectedEventId = (String) masterModel.getValueAt(row, COL_MASTER_EVENT_ID);
            String evName   = (String) masterModel.getValueAt(row, 0);
            double budget   = FinanceManager.getEventBudget(selectedEventId);
            double spent    = FinanceManager.getEventSpent(selectedEventId);
            double remain   = budget - spent;

            detailHeader.setText(String.format(
                "  %s  —  Budget: ₱%.2f  |  Spent: ₱%.2f  |  Remaining: ₱%.2f",
                evName, budget, spent, remain));

            detailModel.setRowCount(0);
            for (FinanceTransaction t : FinanceManager.getExpensesForEvent(selectedEventId)) {
                detailModel.addRow(new Object[]{
                    t.date,
                    t.description,
                    String.format("₱%.2f", t.amount),
                    t.txId  // hidden
                });
            }

            addExpBtn.setEnabled(true);
            delExpBtn.setEnabled(false); // reset; re-enabled by table selection listener
        }

        /** After a full refresh, try to re-select the same event row by eventId. */
        private void reSelectEvent(String eventId) {
            for (int i = 0; i < masterModel.getRowCount(); i++) {
                if (eventId.equals(masterModel.getValueAt(i, COL_MASTER_EVENT_ID))) {
                    masterTable.setRowSelectionInterval(i, i);
                    return;
                }
            }
            // Event no longer exists in the table
            selectedEventId = null;
            detailModel.setRowCount(0);
            addExpBtn.setEnabled(false);
            delExpBtn.setEnabled(false);
        }

        // ── Helpers ───────────────────────────────────────────────────────────────

        /** Hide a column visually while keeping its data in the model. */
        private static void hideCol(JTable t, int col) {
            t.getColumnModel().getColumn(col).setMinWidth(0);
            t.getColumnModel().getColumn(col).setMaxWidth(0);
            t.getColumnModel().getColumn(col).setWidth(0);
        }

        /** Create a summary card label. */
        JLabel summCard(String title, Color bg) {
            JLabel l = new JLabel(summHtml(title, 0), SwingConstants.CENTER);
            l.setOpaque(true);
            l.setBackground(bg);
            l.setForeground(Color.WHITE);
            l.setFont(UITheme.FONT_BODY_BOLD);
            l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(14, 10, 14, 10)));
            return l;
        }
    }

    // ============================================================
    //  ADD BUDGET DIALOG
    //  FIX 2 — Only Upcoming and Happening events are shown.
    //           Completed events cannot receive new budget entries.
    //  FIX 3 — Parent type changed from JFrame to Window so the
    //           cast from SwingUtilities.getWindowAncestor() never fails.
    // ============================================================
    static class AddBudgetDialog extends JDialog {
        private JComboBox<String> eventCombo;
        private JTextField        dateField, descField, amountField;
        /** eventId values parallel to the combo entries */
        private final List<String> eventIds = new ArrayList<>();

        AddBudgetDialog(Window parent) {
            super(parent, "Add Budget to Event", ModalityType.APPLICATION_MODAL);
            setSize(460, 340);
            setLocationRelativeTo(parent);
            setResizable(false);
            buildUI();
        }

        void buildUI() {
            getContentPane().setBackground(UITheme.SURFACE);
            setLayout(new BorderLayout(0, 0));

            // FIX 2: filter — only Upcoming and Happening events may receive budgets.
            List<Event> all = EventManager.loadAll();
            List<Event> eligible = new ArrayList<>();
            for (Event ev : all) {
                if (!ev.isCompleted()) eligible.add(ev);
            }

            if (eligible.isEmpty()) {
                // Show an informative panel instead of a broken empty combo
                JLabel msg = new JLabel(
                    "<html><center><b>No eligible events found.</b><br><br>"
                    + "Budgets can only be added to <b>Upcoming</b> or <b>Happening</b> events.<br>"
                    + "All current events are Completed, or no events exist yet.</center></html>",
                    SwingConstants.CENTER);
                msg.setFont(UITheme.FONT_BODY);
                msg.setForeground(UITheme.TEXT_SECONDARY);
                msg.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
                JButton close = UITheme.ghostBtn("Close");
                close.addActionListener(e -> dispose());
                JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER));
                bp.setBackground(UITheme.SURFACE); bp.add(close);
                add(UITheme.sectionHeader("Add Event Budget"), BorderLayout.NORTH);
                add(msg, BorderLayout.CENTER);
                add(bp,  BorderLayout.SOUTH);
                return;
            }

            String[] names = new String[eligible.size()];
            for (int i = 0; i < eligible.size(); i++) {
                Event ev  = eligible.get(i);
                names[i]  = ev.name + "  (" + ev.date + "  —  " + ev.status + ")";
                eventIds.add(ev.eventId);
            }
            eventCombo = new JComboBox<>(names);
            eventCombo.setFont(UITheme.FONT_BODY);

            dateField   = UITheme.styledField();
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            descField   = UITheme.styledField();
            descField.setText("Budget allocation");
            amountField = UITheme.styledField();

            JPanel form = new JPanel(new GridLayout(4, 2, 10, 14));
            form.setBackground(UITheme.SURFACE);
            form.setBorder(BorderFactory.createEmptyBorder(22, 28, 14, 28));
            form.add(styledLabel("Event:"));              form.add(eventCombo);
            form.add(styledLabel("Date (yyyy-MM-dd):"));  form.add(dateField);
            form.add(styledLabel("Description:"));        form.add(descField);
            form.add(styledLabel("Amount (₱):"));         form.add(amountField);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            btns.setBackground(UITheme.SURFACE);
            btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            JButton cancel = UITheme.ghostBtn("Cancel");
            JButton save   = UITheme.successBtn("Save Budget");
            btns.add(cancel);
            btns.add(save);

            add(UITheme.sectionHeader("Add Event Budget"), BorderLayout.NORTH);
            add(form,  BorderLayout.CENTER);
            add(btns,  BorderLayout.SOUTH);

            cancel.addActionListener(e -> dispose());
            save.addActionListener(e   -> handleSave());
            amountField.addActionListener(e -> handleSave());
            SwingUtilities.invokeLater(() -> amountField.requestFocusInWindow());
        }

        void handleSave() {
            if (eventIds.isEmpty()) return;
            int    idx     = eventCombo.getSelectedIndex();
            String eventId = eventIds.get(idx);
            String date    = dateField.getText().trim();
            String desc    = descField.getText().trim();
            String amtStr  = amountField.getText().trim();

            if (date.isEmpty() || desc.isEmpty() || amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Required", JOptionPane.WARNING_MESSAGE); return;
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Date must be yyyy-MM-dd.",
                    "Format Error", JOptionPane.WARNING_MESSAGE); return;
            }
            double amt;
            try {
                amt = Double.parseDouble(amtStr);
                if (amt <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a positive number.",
                    "Format Error", JOptionPane.WARNING_MESSAGE); return;
            }
            FinanceManager.add(new FinanceTransaction(
                FinanceManager.generateId(), eventId, date, desc,
                TransactionType.INCOME.getLabel(), amt));
            dispose();
        }

        private JLabel styledLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(UITheme.FONT_BODY_BOLD);
            l.setForeground(UITheme.TEXT_SECONDARY);
            return l;
        }
    }

    // ============================================================
    //  ADD EXPENSE DIALOG
    //  FIX 3 — Parent type changed to Window (no cast needed).
    //           Dialog size increased to 420×360 so all fields are
    //           fully visible and interactive.
    //           eventId is pre-assigned from the selected master row;
    //           the user never has to type or choose it.
    // ============================================================
    static class AddExpenseDialog extends JDialog {
        private final String  eventId;
        private JTextField    dateField, descField, amountField;

        AddExpenseDialog(Window parent, String eventId) {
            super(parent, "Add Expense", ModalityType.APPLICATION_MODAL);
            this.eventId = eventId;
            setSize(440, 380);
            setLocationRelativeTo(parent);
            setResizable(false);
            buildUI();
        }

        void buildUI() {
            getContentPane().setBackground(UITheme.SURFACE);
            setLayout(new BorderLayout(0, 0));

            Event ev = EventManager.findById(eventId);
            String evName = (ev != null) ? ev.name : eventId;

            // Subtitle banner: clearly shows which event this expense is linked to
            JLabel subtitle = new JLabel(
                "<html>&nbsp;&nbsp;Charging expense to: <b>" + evName + "</b></html>");
            subtitle.setFont(UITheme.FONT_BODY);
            subtitle.setForeground(new Color(183,28,28));
            subtitle.setOpaque(true);
            subtitle.setBackground(UITheme.RED_LIGHT);
            subtitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));

            JPanel northArea = new JPanel(new BorderLayout());
            northArea.setBackground(UITheme.SURFACE);
            northArea.add(UITheme.sectionHeader("Add Expense"), BorderLayout.NORTH);
            northArea.add(subtitle,                             BorderLayout.SOUTH);

            // Form fields — created with UITheme.styledField() for proper styling
            dateField   = UITheme.styledField();
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            descField   = UITheme.styledField();
            amountField = UITheme.styledField();

            // GridLayout: 3 rows × 2 cols with generous row gap so fields are
            // clearly separated and clickable at any screen DPI
            JPanel form = new JPanel(new GridLayout(3, 2, 10, 16));
            form.setBackground(UITheme.SURFACE);
            form.setBorder(BorderFactory.createEmptyBorder(24, 28, 18, 28));
            form.add(styledLabel("Date (yyyy-MM-dd):")); form.add(dateField);
            form.add(styledLabel("Description:"));       form.add(descField);
            form.add(styledLabel("Amount (₱):"));        form.add(amountField);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            btns.setBackground(UITheme.SURFACE);
            btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            JButton cancel = UITheme.ghostBtn("Cancel");
            JButton save   = UITheme.dangerBtn("Save Expense");
            btns.add(cancel);
            btns.add(save);

            add(northArea, BorderLayout.NORTH);
            add(form,      BorderLayout.CENTER);
            add(btns,      BorderLayout.SOUTH);

            cancel.addActionListener(e -> dispose());
            save.addActionListener(e   -> handleSave());
            amountField.addActionListener(e -> handleSave());
            // Auto-focus the description field so the user can start typing immediately
            SwingUtilities.invokeLater(() -> descField.requestFocusInWindow());
        }

        void handleSave() {
            String date   = dateField.getText().trim();
            String desc   = descField.getText().trim();
            String amtStr = amountField.getText().trim();

            if (date.isEmpty() || desc.isEmpty() || amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Required", JOptionPane.WARNING_MESSAGE); return;
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Date must be yyyy-MM-dd.",
                    "Format Error", JOptionPane.WARNING_MESSAGE); return;
            }
            double amt;
            try {
                amt = Double.parseDouble(amtStr);
                if (amt <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a positive number.",
                    "Format Error", JOptionPane.WARNING_MESSAGE); return;
            }
            FinanceManager.add(new FinanceTransaction(
                FinanceManager.generateId(), eventId, date, desc,
                TransactionType.EXPENSE.getLabel(), amt));
            dispose();
        }

        private JLabel styledLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(UITheme.FONT_BODY_BOLD);
            l.setForeground(UITheme.TEXT_SECONDARY);
            return l;
        }
    }

    // ============================================================
    //  CERTIFICATES
    // ============================================================
    static class CertificatesPanel extends JPanel {
        JSpinner thresholdSpinner; JLabel statusLabel; DefaultTableModel previewModel;
        CertificatesPanel() { setLayout(new BorderLayout(8,8)); setBorder(BorderFactory.createEmptyBorder(12,12,12,12)); setBackground(UITheme.BG); buildUI(); loadPreview(); }
        void buildUI() {
            JPanel settingsCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            settingsCard.setBackground(UITheme.SURFACE);
            settingsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER,1),
                BorderFactory.createEmptyBorder(4,8,4,8)));
            thresholdSpinner = new JSpinner(new SpinnerNumberModel(100,0,100,5));
            ((JSpinner.DefaultEditor)thresholdSpinner.getEditor()).getTextField().setColumns(4);
            ((JSpinner.DefaultEditor)thresholdSpinner.getEditor()).getTextField().setFont(UITheme.FONT_BODY);
            JButton previewBtn  = UITheme.outlinedBtn("Preview Qualifying");
            JButton generateBtn = UITheme.primaryBtn("Generate Certificates");
            statusLabel = new JLabel("Set attendance threshold and click Generate.");
            statusLabel.setFont(UITheme.FONT_CAPTION); statusLabel.setForeground(UITheme.TEXT_SECONDARY);
            JLabel threshLbl = new JLabel("Attendance Threshold:");
            threshLbl.setFont(UITheme.FONT_BODY);
            settingsCard.add(threshLbl); settingsCard.add(thresholdSpinner); settingsCard.add(new JLabel("%"));
            settingsCard.add(previewBtn); settingsCard.add(generateBtn); settingsCard.add(statusLabel);

            previewModel = new DefaultTableModel(new String[]{"Name","Course","Attended","Attendance %","Will Receive Cert?"},0){public boolean isCellEditable(int r,int c){return false;}};
            JTable preview = new JTable(previewModel);
            UITheme.styleTable(preview);
            preview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            preview.setSelectionBackground(new Color(26, 115, 232));
            preview.setSelectionForeground(Color.WHITE);
            preview.setRowSelectionAllowed(true);

            @SuppressWarnings("serial") DefaultTableCellRenderer colorRend = new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    if (sel) {
                        setBackground(t.getSelectionBackground());
                        setForeground(t.getSelectionForeground());
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else {
                        String cert = t.getModel().getValueAt(row, 4).toString().trim();
                        boolean qualifies = cert.contains("Yes");
                        setBackground(qualifies ? UITheme.GREEN_LIGHT : UITheme.RED_LIGHT);
                        setForeground(qualifies ? new Color(27, 94, 32) : new Color(183, 28, 28));
                        setFont(UITheme.FONT_TABLE_BODY);
                    }
                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return this;
                }
            };
            for (int i = 0; i < 5; i++) preview.getColumnModel().getColumn(i).setCellRenderer(colorRend);

            JPanel tableCard = new JPanel(new BorderLayout());
            tableCard.setBackground(UITheme.SURFACE);
            tableCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER,1));
            tableCard.add(new JScrollPane(preview), BorderLayout.CENTER);

            add(settingsCard, BorderLayout.NORTH);
            add(tableCard,    BorderLayout.CENTER);
            previewBtn.addActionListener(e->loadPreview()); generateBtn.addActionListener(e->doGenerate()); thresholdSpinner.addChangeListener(e->loadPreview());
        }
        void loadPreview() {
            previewModel.setRowCount(0);
            int thresh = (int) thresholdSpinner.getValue();
            int total  = EventManager.loadAll().size();

            // ── Collect row data ──────────────────────────────────────────────────────
            //    [0] name  [1] course  [2] attended  [3] pct%  [4] cert label
            //    [5] qualifies (boolean) — hidden sort key, NOT added to table model
            List<Object[]> rows = new ArrayList<>();
            for (Member m : MemberManager.loadAll()) {
                int     att       = AttendanceManager.countAttended(m.studentId);
                int     pct       = total == 0 ? 0 : (int) Math.round(att * 100.0 / total);
                boolean qualifies = pct >= thresh;
                rows.add(new Object[]{
                    m.name,
                    m.course,
                    att + "/" + total,
                    pct + "%",
                    qualifies ? "✅  Yes" : "❌  No",
                    qualifies    // hidden sort key – not added to table model
                });
            }

            // ── Sort ──────────────────────────────────────────────────────────────────
            //    Primary   : qualified group first  (Yes before No)
            //    Secondary : name A→Z within each group (case-insensitive, null-safe)
            rows.sort(
                Comparator
                    .<Object[], Integer>comparing(row -> (boolean) row[5] ? 0 : 1)
                    .thenComparing(
                        row -> row[0] != null ? ((String) row[0]).trim() : "",
                        String.CASE_INSENSITIVE_ORDER
                    )
            );

            // ── Populate table (5 visible columns only) ───────────────────────────────
            for (Object[] row : rows) {
                previewModel.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4]});
            }
        }
        void doGenerate() {
            if(!CertificateGenerator.isPdfBoxAvailable()){JOptionPane.showMessageDialog(this,"PDFBox not installed.","PDFBox Not Found",JOptionPane.WARNING_MESSAGE);return;}
            File outDir=new File("/Users/calebadrieltingson/Downloads/CERTIFICATES PDF"); if(!outDir.exists())outDir.mkdirs();
            int thresh=(int)thresholdSpinner.getValue(); statusLabel.setText("Generating…"); setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<CertificateGenerator.Report,Void>(){
                protected CertificateGenerator.Report doInBackground(){return CertificateGenerator.generateAll(outDir,thresh);}
                protected void done(){setCursor(Cursor.getDefaultCursor());try{CertificateGenerator.Report rep=get();statusLabel.setText(rep.generated+" certificate(s) generated.");StringBuilder sb=new StringBuilder("<html><b>"+rep.generated+" certificate(s) saved to:</b><br>"+outDir.getAbsolutePath());if(!rep.skipped.isEmpty()){sb.append("<br><br><b>Skipped (").append(rep.skipped.size()).append("):</b><ul>");for(String s:rep.skipped)sb.append("<li>").append(s).append("</li>");sb.append("</ul>");}sb.append("</html>");JOptionPane.showMessageDialog(CertificatesPanel.this,new JLabel(sb.toString()),"Generation Complete",JOptionPane.INFORMATION_MESSAGE);}catch(Exception ex){LOG.log(Level.SEVERE,"Cert gen failed",ex);statusLabel.setText("Error: "+ex.getMessage());}}
            }.execute();
        }
    }

    static class CertificateGenerator {

        // ── Canvas logical dimensions (matches CertificatePanel design) ───────
        private static final int CERT_W = 1024;
        private static final int CERT_H = 720;

        // ── Palette (matches CertificatePanel colour constants) ───────────────
        private static final Color CG_BG_GRID   = new Color(0xE8E9EC);
        private static final Color CG_GRID_LINE  = new Color(0xC5D0E6);
        private static final Color CG_WHITE      = Color.WHITE;
        private static final Color CG_BLUE       = new Color(0x1A73E8);
        private static final Color CG_GREEN      = new Color(0x34A853);
        private static final Color CG_YELLOW     = new Color(0xFBBC04);
        private static final Color CG_RED        = new Color(0xEA4335);
        private static final Color CG_DARK       = new Color(0x202124);
        private static final Color CG_MUTED      = new Color(0x5F6368);
        private static final Color CG_MAROON1    = new Color(0x8B1538);
        private static final Color CG_MAROON2    = new Color(0x711C30);
        private static final Color CG_MAROON3    = new Color(0xA7155B);
        private static final Color CG_PURPLE     = new Color(0x6B3BA8);
        private static final Color CG_PINK       = new Color(0xE91E8C);
        private static final Color CG_TEAL       = new Color(0x14B8A6);
        private static final Color CG_SKIN       = new Color(0xFBBF24);
        private static final Color CG_NAVY       = new Color(0x1E40AF);
        private static final Color CG_CRIMSON    = new Color(0xDC2626);

        static class Report{final int generated;final List<String> skipped;Report(int g,List<String> s){generated=g;skipped=s;}}
        static boolean isPdfBoxAvailable(){try{Class.forName("org.apache.pdfbox.pdmodel.PDDocument");return true;}catch(ClassNotFoundException e){return false;}}

        static Report generateAll(File outputDir,int thresholdPct){
            List<Member> members=MemberManager.loadAll();int total=EventManager.loadAll().size();int generated=0;List<String> skipped=new ArrayList<>();
            for(Member m:members){int att=AttendanceManager.countAttended(m.studentId);int pct=total==0?0:(int)Math.round(att*100.0/total);if(pct>=thresholdPct){try{generateOne(m,att,total,pct,outputDir);generated++;}catch(Throwable e){skipped.add(m.name+" — error: "+e.getMessage());}}else skipped.add(m.name+" — "+pct+"% attendance (below "+thresholdPct+"% threshold)");}
            return new Report(generated,skipped);
        }

        // ── Main entry: render image → embed into PDF page ────────────────────
        static void generateOne(Member m, int att, int total, int pct, File outDir) throws Exception {
            BufferedImage img = renderCertificate(m, att, total, pct);

            org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument();
            try {
                // A4 landscape in PDF points (72 dpi)
                float pw = 842f, ph = 595f;
                org.apache.pdfbox.pdmodel.PDPage page =
                    new org.apache.pdfbox.pdmodel.PDPage(
                        new org.apache.pdfbox.pdmodel.common.PDRectangle(pw, ph));
                doc.addPage(page);

                org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject pdImg =
                    org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory.createFromImage(doc, img);

                org.apache.pdfbox.pdmodel.PDPageContentStream cs =
                    new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page);
                try {
                    // Draw the image filling the entire A4 landscape page
                    cs.drawImage(pdImg, 0, 0, pw, ph);
                } finally {
                    cs.close();
                }

                String safe = m.name.replaceAll("[^a-zA-Z0-9 ]", "").replace(" ", "_");
                doc.save(new File(outDir, safe + "_Certificate.pdf"));
            } finally {
                doc.close();
            }
        }

        // ── Render the full GDSC-style certificate to a BufferedImage ─────────
        private static BufferedImage renderCertificate(Member m, int att, int total, int pct) {
            BufferedImage img = new BufferedImage(CERT_W, CERT_H, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

            cgDrawBackground(g);
            cgDrawCertificateBody(g);
            cgDrawSidebar(g);
            cgDrawHeader(g);
            cgDrawContent(g, m, att, total, pct);
            cgDrawDecorativeShapes(g);
            cgDrawFooter(g);

            g.dispose();
            return img;
        }

        // ── Background: grid ──────────────────────────────────────────────────
        private static void cgDrawBackground(Graphics2D g) {
            g.setColor(CG_BG_GRID);
            g.fillRect(0, 0, CERT_W, CERT_H);
            g.setColor(CG_GRID_LINE);
            g.setStroke(new BasicStroke(1f));
            int gs = 30;
            for (int x = 0; x <= CERT_W; x += gs) g.drawLine(x, 0, x, CERT_H);
            for (int y = 0; y <= CERT_H; y += gs) g.drawLine(0, y, CERT_W, y);
        }

        // ── White certificate body + blue border ──────────────────────────────
        private static void cgDrawCertificateBody(Graphics2D g) {
            final int cx = 30, cy = 50, cw = CERT_W - 60, ch = CERT_H - 100;
            g.setColor(CG_WHITE);
            g.fillRect(cx, cy, cw, ch);
            g.setColor(CG_BLUE);
            g.setStroke(new BasicStroke(3f));
            g.drawRect(cx, cy, cw, ch);
        }

        // ── Four-band coloured sidebar ────────────────────────────────────────
        private static void cgDrawSidebar(Graphics2D g) {
            final int cx = 30, cy = 50, ch = CERT_H - 100, sw = 40;
            Color[] bands = {CG_GREEN, CG_YELLOW, CG_RED, CG_BLUE};
            int bh = ch / bands.length;
            for (int i = 0; i < bands.length; i++) {
                g.setColor(bands[i]);
                g.fillRect(cx, cy + i * bh, sw, bh);
            }
        }

        // ── GDSC logo area + university name ──────────────────────────────────
        private static void cgDrawHeader(Graphics2D g) {
            final int cx = 30, cy = 50;
            final int contentX = cx + 40 + 60;

            // Four-dot GDSC logo
            int lx = contentX - 22, ly = cy + 38, dr = 5;
            Color[] dc = {CG_BLUE, CG_RED, CG_GREEN, CG_YELLOW};
            int[][] off = {{0,0},{dr*2+2,0},{0,dr*2+2},{dr*2+2,dr*2+2}};
            for (int i = 0; i < 4; i++) {
                g.setColor(dc[i]);
                g.fillOval(lx + off[i][0], ly + off[i][1], dr*2, dr*2);
            }

            // "Solution Challenge / Google Developer Student Clubs"
            g.setColor(CG_BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Solution",  contentX - 20, cy + 60);
            g.drawString("Challenge", contentX - 20, cy + 75);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            g.drawString("Google Developer Student Clubs", contentX - 20, cy + 87);

            // Right: university name
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.setColor(CG_MUTED);
            int rx = contentX + 260;
            g.drawString("Google Developer Student Clubs",           rx, cy + 50);
            g.drawString("University of the Immaculate Conception",  rx, cy + 62);
        }

        // ── Main certificate content ──────────────────────────────────────────
        private static void cgDrawContent(Graphics2D g, Member m, int att, int total, int pct) {
            final int cx = 30, cy = 50, cw = CERT_W - 60;
            final int contentX = cx + 40 + 60;
            final int contentW = cw - 40 - 80;

            // "Certificate of Recognition"
            g.setFont(new Font("Arial", Font.BOLD, 46));
            g.setColor(CG_DARK);
            g.drawString("Certificate of Recognition", contentX, cy + 170);

            // "This certificate is proudly presented to"
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(CG_MUTED);
            g.drawString("This certificate is proudly presented to", contentX, cy + 210);

            // Member name — strip any legacy commas for clean certificate display
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(CG_DARK);
            String cleanName = m.name.replace(",", " ").replaceAll("\\s{2,}", " ").trim();
            g.drawString(cleanName, contentX, cy + 275);

            // Position · Course · Year
            g.setFont(new Font("Arial", Font.PLAIN, 15));
            g.setColor(CG_MUTED);
            String details = m.position + " · " + m.course + " · " + m.yearLevel;
            g.drawString(details, contentX, cy + 305);

            // Gradient stats bar
            String statsText = String.format("Attended %d of %d events (%d%%)", att, total, pct);
            int sbX = contentX - 20, sbY = cy + 350, sbW = contentW + 20, sbH = 60;
            GradientPaint gp = new GradientPaint(sbX, sbY, CG_BLUE, sbX + sbW, sbY, CG_YELLOW);
            g.setPaint(gp);
            g.fillRect(sbX, sbY, sbW, sbH);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(CG_WHITE);
            g.drawString(statsText, sbX + 30, sbY + 38);

            // Recognition sub-text
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.setColor(CG_DARK);
            g.drawString(
                "in recognition of outstanding attendance and commitment to the organization.",
                sbX + 30, sbY + sbH + 25);
        }

        // ── Decorative geometric shapes + stick figures ───────────────────────
        private static void cgDrawDecorativeShapes(Graphics2D g) {
            final int cx = 30, cy = 50, cw = CERT_W - 60;
            final int contentX = cx + 40 + 60;
            final int contentW = cw - 40 - 80;

            int sx = contentX + contentW - 200;
            int sy = cy + 200;

            // Large yellow circle
            g.setColor(CG_YELLOW);
            g.fillOval(sx + 50, sy - 100, 100, 100);

            // Row 1: green + blue rectangles
            g.setColor(CG_GREEN);
            g.fillRect(sx + 10, sy + 30, 70, 70);
            g.setColor(CG_BLUE);
            g.fillRect(sx + 85, sy + 30, 70, 70);

            // Chevron icon inside blue box
            g.setColor(CG_WHITE);
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int bx = sx + 85 + 35, bby = sy + 65;
            g.drawPolyline(new int[]{bx-14, bx, bx+14}, new int[]{bby-10, bby, bby-10}, 3);
            g.drawLine(bx, bby, bx, bby + 12);

            // Row 2: maroon circle + rectangles
            g.setStroke(new BasicStroke(1f));
            g.setColor(CG_MAROON1);
            g.fillOval(sx - 90, sy + 60, 120, 120);
            g.setColor(CG_MAROON2);
            g.fillRect(sx + 10, sy + 105, 70, 70);
            g.setColor(CG_MAROON3);
            g.fillRect(sx + 85, sy + 105, 70, 70);
            g.setColor(CG_PURPLE);
            g.fillRect(sx + 160, sy + 105, 70, 70);

            // Row 3: wide pink rectangle
            g.setColor(CG_PINK);
            g.fillRect(sx + 85, sy + 180, 145, 70);

            // Stick figures
            cgDrawStickFigure(g, sx - 30, sy + 50, CG_SKIN, CG_WHITE, CG_NAVY);
            cgDrawStickFigure(g, sx + 200, sy + 80, CG_SKIN, CG_WHITE, CG_CRIMSON);

            // Top-right corner decorations
            int certRight = cx + cw;
            g.setColor(new Color(0xE9, 0x1E, 0x8C, 0x33));
            g.fillOval(certRight - 140, cy + 60, 80, 80);
            g.setColor(CG_TEAL);
            g.setStroke(new BasicStroke(3f));
            for (int i = 0; i < 3; i++)
                g.drawLine(certRight - 150, cy + 90 + i * 10, certRight - 120, cy + 90 + i * 10);

            // Blue rotated diamond
            g.setColor(CG_BLUE);
            Graphics2D dg = (Graphics2D) g.create();
            dg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dg.translate(certRight - 50, cy + 130);
            dg.rotate(Math.PI / 4);
            dg.fillRect(-8, -8, 16, 16);
            dg.dispose();
            g.setStroke(new BasicStroke(1f));
        }

        // ── Stick figure helper ───────────────────────────────────────────────
        private static void cgDrawStickFigure(Graphics2D g, int cx, int cy,
                                               Color skin, Color shirt, Color trousers) {
            int r = 15;
            g.setColor(skin);
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
            g.setColor(shirt);
            g.fillRect(cx - r, cy + r, r * 2, 40);
            g.setColor(trousers);
            g.fillPolygon(new int[]{cx - r, cx + r, cx},
                          new int[]{cy + r + 40, cy + r + 40, cy + r + 85}, 3);
        }

        // ── Footer: system name + award date ─────────────────────────────────
        private static void cgDrawFooter(Graphics2D g) {
            final int cx = 30, cy = 50, ch = CERT_H - 100, cw = CERT_W - 60;
            final int contentX = cx + 40 + 60;
            final int contentW = cw - 40 - 80;
            int footY = cy + ch - 30;

            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.setColor(CG_BLUE);
            g.drawString("Student Organization Management System", contentX, footY);

            String dateStr = "Awarded: " + new SimpleDateFormat("MMMM d, yyyy").format(new Date());
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.setColor(CG_MUTED);
            FontMetrics fm = g.getFontMetrics();
            int dw = fm.stringWidth(dateStr);
            g.drawString(dateStr, contentX + contentW - dw, footY);
        }
    }

    // ============================================================
    //  CHECK-IN DIALOG
    // ============================================================
    static class CheckInDialog extends JDialog {
        private static final Logger CLOG = Logger.getLogger("CheckInDialog");
        private final Event             event;
        private final DefaultTableModel logModel;
        private final JLabel            statusLabel;
        private final JLabel            countLabel;
        private final JTextField        inputField;

        private final boolean           eventNotStarted;
        private final boolean           eventLocked;
        private final boolean           adminOverride;

        private volatile boolean running    = false;
        private volatile String  lastId     = "";
        private volatile long    lastScanMs  = 0;
        private static final long DEBOUNCE_MS = 2500;
        private volatile String  candidateId    = "";
        private volatile int     candidateCount = 0;
        private static final int CONFIRM_NEEDED = 2;
        private FramePreviewPanel previewPanel;
        private final java.util.concurrent.BlockingQueue<BufferedImage> scanQueue =
            new java.util.concurrent.ArrayBlockingQueue<>(4);
        static final String[] LOG_COLS = {"Time","Student ID","Name","Course","Year"};
        private static final String[] FFMPEG_CANDIDATES = {"/opt/homebrew/bin/ffmpeg","/usr/local/bin/ffmpeg"};

        CheckInDialog(JFrame parent, Event event, boolean adminOverride) {
            super(parent, "📷  Camera Check-In — "+event.name, true);
            this.event         = event;
            this.adminOverride = adminOverride;
            this.eventLocked   = event.isCompleted();
            this.eventNotStarted = event.isUpcoming();

            logModel    = new DefaultTableModel(LOG_COLS,0){public boolean isCellEditable(int r,int c){return false;}};
            statusLabel = new JLabel("📷  Initializing camera...");
            countLabel  = new JLabel("Checked in: 0");
            inputField  = new JTextField();
            previewPanel = new FramePreviewPanel();

            setSize(960,660);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            buildUI();
            refreshTable();
            refreshCount();

            if (eventLocked && !adminOverride) {
                inputField.setEnabled(false);
                inputField.setToolTipText("Check-in disabled: event is Completed");
            }
            if (eventNotStarted) {
                inputField.setEnabled(false);
                inputField.setToolTipText("Check-in disabled: event has not started yet (status: Upcoming)");
            }

            addWindowListener(new java.awt.event.WindowAdapter(){
                public void windowOpened(java.awt.event.WindowEvent e) { startCamera(); }
                public void windowClosing(java.awt.event.WindowEvent e){ stopCamera(); dispose(); }
            });
        }

        void buildUI() {
            setLayout(new BorderLayout());
            getContentPane().setBackground(UITheme.BG);

            JPanel header = new JPanel(new GridLayout(2,1,2,2));
            header.setBackground(new Color(32,33,36));
            header.setBorder(BorderFactory.createEmptyBorder(12,18,12,18));
            JLabel titleLbl = new JLabel("📷  "+event.name+"   ·   "+event.date);
            titleLbl.setFont(UITheme.FONT_SUBHEADING); titleLbl.setForeground(Color.WHITE);
            JLabel instrLbl = new JLabel("Hold student ID barcode/QR to webcam — or type Student ID + Enter.");
            instrLbl.setFont(UITheme.FONT_CAPTION); instrLbl.setForeground(new Color(180,200,230));
            header.add(titleLbl); header.add(instrLbl);

            JPanel lockBanner = null;
            if (eventNotStarted) {
                lockBanner = new JPanel(new BorderLayout());
                lockBanner.setBackground(UITheme.ORANGE);
                lockBanner.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
                JLabel l = new JLabel(
                    "⏳  EVENT NOT STARTED — Check-in is only allowed when the event is Happening.");
                l.setFont(UITheme.FONT_BODY_BOLD); l.setForeground(Color.WHITE);
                lockBanner.add(l, BorderLayout.WEST);
            } else if (eventLocked) {
                lockBanner = new JPanel(new BorderLayout());
                lockBanner.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
                if (adminOverride) {
                    lockBanner.setBackground(new Color(46,140,60));
                    JLabel l = new JLabel("🔓  ADMIN OVERRIDE — Editing attendance for completed event.");
                    l.setFont(UITheme.FONT_BODY_BOLD); l.setForeground(Color.WHITE);
                    lockBanner.add(l, BorderLayout.WEST);
                } else {
                    lockBanner.setBackground(UITheme.GOOGLE_RED);
                    JLabel l = new JLabel("🔒  EVENT COMPLETED — Check-in disabled. Use Admin Override to edit.");
                    l.setFont(UITheme.FONT_BODY_BOLD); l.setForeground(Color.WHITE);
                    lockBanner.add(l, BorderLayout.WEST);
                }
            }

            JPanel statusBar = new JPanel(new BorderLayout(6,0));
            statusBar.setBackground(UITheme.SURFACE);
            statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(10,16,10,16)));
            statusLabel.setFont(UITheme.FONT_BODY_BOLD);
            countLabel.setFont(UITheme.FONT_BODY_BOLD); countLabel.setForeground(UITheme.GOOGLE_BLUE);
            statusBar.add(statusLabel,BorderLayout.WEST);
            statusBar.add(countLabel, BorderLayout.EAST);

            JPanel topArea = new JPanel(new BorderLayout());
            topArea.add(header, BorderLayout.NORTH);
            if (lockBanner != null) topArea.add(lockBanner, BorderLayout.CENTER);
            topArea.add(statusBar, BorderLayout.SOUTH);

            previewPanel.setPreferredSize(new Dimension(440,330));

            JPanel inputPanel = new JPanel(new BorderLayout(6,0));
            inputPanel.setBackground(UITheme.SURFACE);
            inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER),
                    "Manual Entry / USB Scanner — type Student ID + Enter"),
                BorderFactory.createEmptyBorder(4,8,4,8)));
            inputField.setFont(UITheme.FONT_MONO);
            inputField.setPreferredSize(new Dimension(0,38));
            inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER,1,true),
                BorderFactory.createEmptyBorder(6,10,6,10)));

            JButton checkBtn = UITheme.successBtn("✔  Check In");
            if ((eventLocked && !adminOverride) || eventNotStarted) {
                checkBtn.setEnabled(false);
            }
            inputPanel.add(inputField, BorderLayout.CENTER);
            inputPanel.add(checkBtn,   BorderLayout.EAST);

            JPanel leftPanel = new JPanel(new BorderLayout(0,8));
            leftPanel.setBackground(UITheme.BG);
            leftPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,6));
            leftPanel.add(previewPanel,BorderLayout.CENTER);
            leftPanel.add(inputPanel,  BorderLayout.SOUTH);

            JTable logTable = new JTable(logModel);
            UITheme.styleTable(logTable);

            JPanel rightPanel = new JPanel(new BorderLayout(0,8));
            rightPanel.setBackground(UITheme.SURFACE);
            rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,1,0,0,UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(10,8,0,10)));
            JLabel logTitle = new JLabel("  ✅  Check-In Log");
            logTitle.setFont(UITheme.FONT_BODY_BOLD); logTitle.setForeground(UITheme.TEXT_PRIMARY);
            rightPanel.add(logTitle,               BorderLayout.NORTH);
            rightPanel.add(new JScrollPane(logTable), BorderLayout.CENTER);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            split.setResizeWeight(0.52); split.setDividerSize(4); split.setBackground(UITheme.BG);

            JButton closeBtn = UITheme.outlinedBtn("Close");
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,10));
            bottomPanel.setBackground(UITheme.SURFACE);
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            bottomPanel.add(closeBtn);

            add(topArea,     BorderLayout.NORTH);
            add(split,       BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            java.awt.event.ActionListener doCheckIn = e -> {
                String id = inputField.getText().trim();
                inputField.setText(""); inputField.requestFocusInWindow();
                if (!id.isEmpty()) processInput(id);
            };
            inputField.addActionListener(doCheckIn);
            checkBtn.addActionListener(doCheckIn);
            closeBtn.addActionListener(e -> { stopCamera(); dispose(); });

            if (eventNotStarted)
                setStatus("⏳  Event has not started — check-in only allowed for Happening events", UITheme.ORANGE);
            else if (eventLocked && !adminOverride)
                setStatus("🔒  Event is Completed — check-in disabled", UITheme.GOOGLE_RED);
            else if (eventLocked)
                setStatus("🔓  Admin Override active — check-in enabled", UITheme.GOOGLE_GREEN);
        }

        void startCamera() {
            String ffmpeg = findFfmpeg();
            if (ffmpeg == null) { showInstallInstructions(); return; }
            running = true;
            setStatus("📷  Starting camera — please allow access if prompted...", UITheme.GOOGLE_BLUE);

            Thread captureThread = new Thread(() -> {
                Process proc = null;
                try {
                    ProcessBuilder pb = new ProcessBuilder(ffmpeg,"-f","avfoundation","-framerate","30","-i","0","-an","-vf","scale=640:480","-f","mjpeg","-qscale:v","3","-r","30","pipe:1");
                    pb.redirectError(ProcessBuilder.Redirect.DISCARD);
                    proc = pb.start();
                    java.io.InputStream in = new java.io.BufferedInputStream(proc.getInputStream(), 262144);
                    boolean firstFrame = true;
                    while (running) {
                        BufferedImage frame = readMjpegFrame(in);
                        if (frame == null) break;
                        final BufferedImage img = frame;
                        final boolean announce = firstFrame;
                        if (firstFrame) firstFrame = false;
                        SwingUtilities.invokeLater(() -> {
                            previewPanel.setFrame(img);
                            if (announce && !eventNotStarted && (!eventLocked || adminOverride))
                                setStatus("✅  Camera ready — hold barcode / QR code up to scan", UITheme.GOOGLE_GREEN);
                        });
                        if (!eventNotStarted && (!eventLocked || adminOverride))
                            scanQueue.offer(img);
                    }
                } catch (Exception ex) {
                    if (running) CLOG.log(Level.WARNING, "ffmpeg capture error", ex);
                } finally {
                    if (proc != null) proc.destroyForcibly();
                }
                if (running)
                    SwingUtilities.invokeLater(() ->
                        setStatus("⚠  Camera stream ended. Check camera permissions.", UITheme.ORANGE));
            }, "ffmpeg-capture");
            captureThread.setDaemon(true); captureThread.start();

            Thread scanThread = new Thread(() -> {
                while (running) {
                    try {
                        BufferedImage img = scanQueue.poll(300, TimeUnit.MILLISECONDS);
                        if (img != null) performScan(img);
                    } catch (InterruptedException e) { break; }
                }
            }, "zxing-scanner");
            scanThread.setDaemon(true); scanThread.start();
        }

        private static BufferedImage readMjpegFrame(java.io.InputStream in) throws java.io.IOException {
            int prev=-1,cur;
            while ((cur = in.read()) != -1) { if (prev == 0xFF && cur == 0xD8) break; prev = cur; }
            if (cur == -1) return null;
            ByteArrayOutputStream buf = new ByteArrayOutputStream(65536);
            buf.write(0xFF); buf.write(0xD8); prev = -1;
            while ((cur = in.read()) != -1) {
                buf.write(cur);
                if (prev == 0xFF && cur == 0xD9) {
                    try { return ImageIO.read(new java.io.ByteArrayInputStream(buf.toByteArray())); }
                    catch (Exception e) { return null; }
                }
                prev = cur;
            }
            return null;
        }

        void stopCamera() {
            running = false;
            try { new ProcessBuilder("pkill","-f","ffmpeg.*avfoundation").start().waitFor(1,TimeUnit.SECONDS); }
            catch (Exception ignored) {}
        }
        static String findFfmpeg() {
            for (String p : FFMPEG_CANDIDATES) if (new File(p).canExecute()) return p;
            return null;
        }

        void showInstallInstructions() {
            SwingUtilities.invokeLater(() -> {
                previewPanel.showMessage(new String[]{
                    "📷  Camera setup needed","",
                    "ffmpeg is not installed yet.",
                    "Run these commands in Terminal:","",
                    "1.  /bin/bash -c \"$(curl -fsSL",
                    "    https://raw.githubusercontent.com/",
                    "    Homebrew/install/HEAD/install.sh)\"","",
                    "2.  brew install ffmpeg","",
                    "Then restart the app.","",
                    "✅  Manual entry below works now."
                });
                if (!eventNotStarted && (!eventLocked || adminOverride)) {
                    setStatus("⌨  ffmpeg not found — use manual entry below", UITheme.GOOGLE_BLUE);
                    inputField.requestFocusInWindow();
                }
            });
        }

        void performScan(BufferedImage img) {
            if (eventNotStarted) return;
            if (eventLocked && !adminOverride) return;

            try {
                com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(
                    new com.google.zxing.common.HybridBinarizer(
                        new com.google.zxing.client.j2se.BufferedImageLuminanceSource(img)));
                Map<com.google.zxing.DecodeHintType,Object> hints = new HashMap<>();
                hints.put(com.google.zxing.DecodeHintType.TRY_HARDER, Boolean.TRUE);
                hints.put(com.google.zxing.DecodeHintType.POSSIBLE_FORMATS, java.util.Arrays.asList(
                    com.google.zxing.BarcodeFormat.QR_CODE,
                    com.google.zxing.BarcodeFormat.CODE_128,
                    com.google.zxing.BarcodeFormat.CODE_39,
                    com.google.zxing.BarcodeFormat.EAN_13,
                    com.google.zxing.BarcodeFormat.EAN_8,
                    com.google.zxing.BarcodeFormat.ITF,
                    com.google.zxing.BarcodeFormat.PDF_417,
                    com.google.zxing.BarcodeFormat.DATA_MATRIX));
                com.google.zxing.Result result =
                    new com.google.zxing.MultiFormatReader().decode(bitmap, hints);
                String decoded = result.getText().trim();

                if (decoded.equals(candidateId)) { candidateCount++; }
                else { candidateId = decoded; candidateCount = 1; }
                if (candidateCount < CONFIRM_NEEDED) return;
                candidateCount = 0;

                long now = System.currentTimeMillis();
                if (decoded.equals(lastId) && (now - lastScanMs) < DEBOUNCE_MS) return;
                lastId = decoded; lastScanMs = now;
                SwingUtilities.invokeLater(() -> processInput(decoded));
            } catch (com.google.zxing.NotFoundException ignored) {
                candidateId = ""; candidateCount = 0;
            } catch (Exception e) {
                CLOG.log(Level.FINE, "ZXing error", e);
            }
        }

        /**
         * Central entry point for every check-in attempt — camera scan or manual input.
         *
         * Validation order:
         *   1. Empty input guard
         *   2. Event status check via validateCheckIn() — blocks Upcoming / Completed-no-override
         *   3. Member existence check — STRICT: ID must exist in members.json right now
         *      (cache invalidated on every delete, so this is always fresh)
         *   4. Duplicate check-in guard
         *   5. Record and notify
         *
         * FIX 3 — ID EXISTENCE IS RE-VALIDATED ON EVERY SCAN
         * Because MemberManager.delete() invalidates the DataCache, the very next
         * call to MemberManager.idExists() reads directly from the file and will
         * correctly return false for a deleted ID.  No stale-cache path can sneak
         * a deleted ID through.
         */
        void processInput(String rawId) {
            String sid = rawId.trim();
            if (sid.isEmpty()) return;

            // ── Step 1: Re-validate event status (handles date rollover while dialog is open)
            Event fresh = EventManager.findById(event.eventId);
            Event toCheck = (fresh != null) ? fresh : event;
            ValidationResult statusCheck = validateCheckIn(toCheck, adminOverride);
            if (!statusCheck.isValid()) {
                setStatus("⛔  " + statusCheck.getMessage().split("\n")[0], UITheme.ORANGE);
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            // ── Step 2: Strict member existence check
            //    DataCache was invalidated by MemberManager.delete(), so loadAll()
            //    reads the file fresh and will NOT find a deleted ID.
            if (!MemberManager.idExists(sid)) {
                setStatus("❌  Unknown Student ID: " + sid + " — not found in member records", UITheme.GOOGLE_RED);
                Toolkit.getDefaultToolkit().beep();
                CLOG.log(Level.WARNING, "Check-in rejected: studentId ''{0}'' not in members.json", sid);
                return;
            }

            // ── Step 3: Duplicate guard
            //    getAttendees() runs the orphan-check, so the list it returns
            //    only contains valid IDs.
            List<String> attendees = AttendanceManager.getAttendees(event.eventId);
            if (attendees.contains(sid)) {
                setStatus("⚠  Already checked in: " + getMemberName(sid), UITheme.GOOGLE_YELLOW);
                return;
            }

            // ── Step 4: Record attendance
            attendees.add(sid);
            AttendanceManager.saveAttendance(event.eventId, attendees);
            String name = getMemberName(sid);
            refreshTable();
            refreshCount();
            setStatus("✅  Checked in: " + name, UITheme.GOOGLE_GREEN);
            Toolkit.getDefaultToolkit().beep();
            notifyAttendanceChanged();
        }

        /**
         * FIX 4 — NEVER SHOW "unknown" IN THE LOG
         *
         * Original code:
         *   logModel.addRow(m != null
         *       ? new Object[]{time, sid, m.name, ...}
         *       : new Object[]{time, sid, "(unknown)", "—", "—"});   ← BUG
         *
         * Fixed code: skip any SID that can't be resolved to a member.
         * AttendanceManager.getAttendees() already performs orphan cleanup,
         * so reaching a null getMember() here is a logic error — we log it
         * and skip rather than rendering an invalid row.
         */
        void refreshTable() {
            logModel.setRowCount(0);
            String time = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            for (String sid : AttendanceManager.getAttendees(event.eventId)) {
                Member m = getMember(sid);
                if (m == null) {
                    // Should not happen after cascade delete + orphan guard,
                    // but guard defensively rather than ever printing "(unknown)".
                    CLOG.log(Level.WARNING,
                        "refreshTable: SID ''{0}'' has no member record — skipped from log.", sid);
                    continue;
                }
                logModel.addRow(new Object[]{time, sid, m.name, m.course, m.yearLevel});
            }
        }

        void refreshCount() {
            int cnt = AttendanceManager.getAttendees(event.eventId).size();
            countLabel.setText("Checked in: " + cnt + " / " + MemberManager.loadAll().size() + " members");
        }
        void setStatus(String msg, Color color) { statusLabel.setText(msg); statusLabel.setForeground(color); }
        String getMemberName(String sid)  { Member m = getMember(sid); return m != null ? m.name : sid; }
        Member getMember(String sid) {
            for (Member m : MemberManager.loadAll()) if (m.studentId.equals(sid)) return m;
            return null;
        }

        static class FramePreviewPanel extends JPanel {
            private volatile BufferedImage frame = null;
            private String[] messageLines = null;
            FramePreviewPanel() { setBackground(new Color(14,20,36)); }
            void setFrame(BufferedImage f)      { this.frame = f; this.messageLines = null; repaint(); }
            void showMessage(String[] lines)    { this.messageLines = lines; this.frame = null; repaint(); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                if (frame != null) {
                    g2.drawImage(frame,0,0,w,h,null);
                    int rw=(int)(w*0.55),rh=(int)(h*0.40),rx=(w-rw)/2,ry=(h-rh)/2;
                    g2.setColor(new Color(255,255,255,160));
                    g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,0,new float[]{10f,6f},0f));
                    g2.drawRect(rx,ry,rw,rh);
                    g2.setStroke(new BasicStroke(3f)); g2.setColor(new Color(100,220,100,200));
                    int cs=18;
                    g2.drawLine(rx,ry,rx+cs,ry);        g2.drawLine(rx,ry,rx,ry+cs);
                    g2.drawLine(rx+rw,ry,rx+rw-cs,ry);  g2.drawLine(rx+rw,ry,rx+rw,ry+cs);
                    g2.drawLine(rx,ry+rh,rx+cs,ry+rh);  g2.drawLine(rx,ry+rh,rx,ry+rh-cs);
                    g2.drawLine(rx+rw,ry+rh,rx+rw-cs,ry+rh); g2.drawLine(rx+rw,ry+rh,rx+rw,ry+rh-cs);
                    g2.setFont(UITheme.FONT_CAPTION); g2.setColor(new Color(255,255,255,160));
                    FontMetrics fm = g2.getFontMetrics();
                    String hint = "Point barcode / QR code here";
                    g2.drawString(hint,(w-fm.stringWidth(hint))/2,ry-5);
                } else {
                    g2.setColor(new Color(14,20,36)); g2.fillRect(0,0,w,h);
                    String[] lines = messageLines != null ? messageLines
                        : new String[]{"📷  Starting camera...","","Please wait..."};
                    int y = h/2-(lines.length*18)/2;
                    for (String line : lines) {
                        boolean isCode = line.startsWith("1.") || line.startsWith("2.") || line.startsWith("    http");
                        g2.setFont(new Font(isCode?"Monospaced":"SansSerif",
                            line.startsWith("📷")||line.startsWith("✅")?Font.BOLD:Font.PLAIN, 12));
                        g2.setColor(line.startsWith("✅") ? new Color(80,210,130)
                            : isCode ? new Color(255,210,100)
                            : line.isEmpty() ? new Color(14,20,36)
                            : new Color(160,185,230));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(line,(w-fm.stringWidth(line))/2,y); y+=20;
                    }
                }
                g2.dispose();
            }
        }
    }

    // ============================================================
    //  MEMBERS PANEL
    // ============================================================
    static class MembersPanel extends JPanel {
        JTable table; DefaultTableModel tableModel;
        JTextField searchField; JComboBox<String> statusFilter, searchCategory;
        JLabel attendNameLabel, attendSummaryLabel;
        JTable sheetTable; DefaultTableModel sheetModel;
        JButton saveAttendBtn; String currentMemberId=null;
        private final List<Boolean> rowLocked=new ArrayList<>();
        private boolean adminOverrideActive=false;
        private JButton adminOverrideBtn;

        static final String[] COLUMNS    ={"Student ID","Name","Course","Year","Position","Status","Attendance"};
        static final String[] SHEET_COLS ={"Event Name","Date","Status","Present"};
        static final String[] SEARCH_CATS={"All Fields","Student ID","Name","Course","Year Level","Position","Status"};

        MembersPanel(){
            setLayout(new BorderLayout(6,6));
            setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            setBackground(UITheme.BG);
            buildUI(); loadTable();
            globalAttendanceListener=this::refreshAttendanceColumn;
        }

        void refreshAttendanceColumn(){
            DataCache.invalidateAttendance();
            int totalEvents=EventManager.loadAll().size();
            for(int row=0;row<tableModel.getRowCount();row++){String sid=(String)tableModel.getValueAt(row,0);tableModel.setValueAt(AttendanceManager.countAttended(sid)+"/"+totalEvents,row,6);}
            if(currentMemberId!=null)refreshSheetPanel();
        }

        void buildUI(){
            JPanel top = UITheme.toolbarPanel();
            JButton addBtn  = UITheme.primaryBtn("Add Member");
            JButton editBtn = UITheme.outlinedBtn("Edit");
            JButton delBtn  = UITheme.dangerBtn("Delete");
            top.add(addBtn); top.add(editBtn); top.add(delBtn);

            tableModel=new DefaultTableModel(COLUMNS,0){public boolean isCellEditable(int r,int c){return false;}};
            table=new JTable(tableModel);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            UITheme.styleTable(table);

            JPanel right=new JPanel(new BorderLayout(4,6));
            right.setBackground(UITheme.SURFACE);
            right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,1,0,0,UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(8,10,8,10)));
            right.setPreferredSize(new Dimension(350,0));

            attendNameLabel=new JLabel("Select a member");
            attendNameLabel.setFont(UITheme.FONT_BODY_BOLD);
            attendNameLabel.setForeground(UITheme.TEXT_PRIMARY);

            attendSummaryLabel=new JLabel(" ");
            attendSummaryLabel.setFont(UITheme.FONT_CAPTION);
            attendSummaryLabel.setForeground(UITheme.TEXT_SECONDARY);

            JPanel info=new JPanel(new GridLayout(2,1,2,4));
            info.setBackground(UITheme.SURFACE);
            info.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
            info.add(attendNameLabel); info.add(attendSummaryLabel);

            JLabel sheetHdr = new JLabel("Attendance Sheet");
            sheetHdr.setFont(UITheme.FONT_BODY_BOLD);
            sheetHdr.setForeground(UITheme.TEXT_SECONDARY);
            sheetHdr.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));

            JPanel infoFull = new JPanel(new BorderLayout());
            infoFull.setBackground(UITheme.SURFACE);
            infoFull.add(sheetHdr, BorderLayout.NORTH);
            infoFull.add(info, BorderLayout.CENTER);

            sheetModel=new DefaultTableModel(SHEET_COLS,0){
                @Override public boolean isCellEditable(int r,int c){if(c!=3)return false;if(r>=rowLocked.size())return false;return!rowLocked.get(r)||adminOverrideActive;}
                @Override public Class<?> getColumnClass(int c){return c==3?Boolean.class:String.class;}
            };
            sheetTable=new JTable(sheetModel);
            sheetTable.setRowHeight(28); sheetTable.getTableHeader().setReorderingAllowed(false);
            sheetTable.setFont(UITheme.FONT_TABLE_BODY);
            sheetTable.setGridColor(UITheme.DIVIDER);
            sheetTable.getColumnModel().getColumn(0).setPreferredWidth(120);
            sheetTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            sheetTable.getColumnModel().getColumn(2).setPreferredWidth(95);
            sheetTable.getColumnModel().getColumn(3).setPreferredWidth(55);
            UITheme.styleTableHeader(sheetTable, UITheme.GOOGLE_BLUE);

            @SuppressWarnings("serial") DefaultTableCellRenderer sheetRend=new DefaultTableCellRenderer(){
                @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                    super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                    if(!sel){
                        boolean locked=row<rowLocked.size()&&rowLocked.get(row);
                        if(locked&&!adminOverrideActive){
                            setBackground(new Color(235,235,235));
                            setForeground(new Color(130,130,130));
                            setFont(UITheme.FONT_CAPTION);
                        } else if(col==2){
                            String val = v==null?"":v.toString();
                            if(val.contains("Completed")){
                                setBackground(new Color(232,234,237));
                                setForeground(new Color(60,60,60));
                                setFont(UITheme.FONT_TABLE_BODY);
                            } else if(val.contains("Happening")){
                                setBackground(new Color(252,228,225));
                                setForeground(new Color(180,30,20));
                                setFont(UITheme.FONT_BODY_BOLD);
                            } else {
                                setBackground(new Color(232,240,254));
                                setForeground(new Color(26,115,232));
                                setFont(UITheme.FONT_TABLE_BODY);
                            }
                        } else {
                            setBackground(row%2==0?UITheme.SURFACE:UITheme.SURFACE_ALT);
                            setForeground(UITheme.TEXT_PRIMARY);
                            setFont(UITheme.FONT_TABLE_BODY);
                        }
                    }
                    return this;
                }
            };
            sheetTable.getColumnModel().getColumn(0).setCellRenderer(sheetRend);
            sheetTable.getColumnModel().getColumn(1).setCellRenderer(sheetRend);
            sheetTable.getColumnModel().getColumn(2).setCellRenderer(sheetRend);

            saveAttendBtn   = UITheme.primaryBtn("Save Attendance");
            saveAttendBtn.setEnabled(false);
            adminOverrideBtn = UITheme.warningBtn("Admin: Edit Locked");
            adminOverrideBtn.setFont(UITheme.FONT_CAPTION);
            adminOverrideBtn.setEnabled(false);
            adminOverrideBtn.addActionListener(e->toggleAdminOverride());

            JPanel sbp=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,4));
            sbp.setBackground(UITheme.SURFACE);
            sbp.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            sbp.add(adminOverrideBtn); sbp.add(saveAttendBtn);

            right.add(infoFull,BorderLayout.NORTH);
            right.add(new JScrollPane(sheetTable),BorderLayout.CENTER);
            right.add(sbp,BorderLayout.SOUTH);

            JPanel tableCard = new JPanel(new BorderLayout());
            tableCard.setBackground(UITheme.SURFACE);
            tableCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER,1));
            tableCard.add(top, BorderLayout.NORTH);
            tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

            JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard, right);
            split.setResizeWeight(0.70); split.setDividerSize(4); split.setBorder(null);

            JPanel bot=new JPanel(new FlowLayout(FlowLayout.LEFT,8,6));
            bot.setBackground(UITheme.SURFACE);
            bot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(4,8,4,8)));
            searchCategory=new JComboBox<>(SEARCH_CATS);
            searchCategory.setFont(UITheme.FONT_BODY);
            searchField=new JTextField(16);
            searchField.setFont(UITheme.FONT_BODY);
            searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER,1,true),BorderFactory.createEmptyBorder(5,10,5,10)));
            statusFilter=new JComboBox<>(prependAll(MemberStatus.labels()));
            statusFilter.setFont(UITheme.FONT_BODY);
            JButton srchBtn=UITheme.primaryBtn("Search");
            JButton clrBtn =UITheme.ghostBtn("Show All");
            clrBtn.setForeground(UITheme.GOOGLE_BLUE);
            JLabel statusLbl = new JLabel("Status:");
            statusLbl.setFont(UITheme.FONT_BODY);
            bot.add(new JLabel("Search by:")); bot.add(searchCategory); bot.add(searchField);
            bot.add(srchBtn); bot.add(clrBtn); bot.add(statusLbl); bot.add(statusFilter);

            add(split, BorderLayout.CENTER);
            add(bot,   BorderLayout.SOUTH);

            addBtn.addActionListener(e->openAddDialog());
            editBtn.addActionListener(e->openEditDialog());
            delBtn.addActionListener(e->openDeleteDialog());
            srchBtn.addActionListener(e->doSearch());
            searchField.addActionListener(e->doSearch());
            statusFilter.addActionListener(e->doSearch());
            clrBtn.addActionListener(e->{searchField.setText("");searchCategory.setSelectedIndex(0);statusFilter.setSelectedIndex(0);loadTable();});
            table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())refreshSheetPanel();});
            saveAttendBtn.addActionListener(e->saveAttendanceFromSheet());
        }

        void toggleAdminOverride(){
            if(!adminOverrideActive){int conf=JOptionPane.showConfirmDialog(this,"<html><b>Enable Admin Override?</b><br><br>This allows editing attendance for <b>Completed</b> events.<br>Changes will be saved permanently.</html>","Admin Override",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);if(conf!=JOptionPane.YES_OPTION)return;adminOverrideActive=true;adminOverrideBtn.setText("Disable Override");adminOverrideBtn.setBackground(UITheme.GOOGLE_RED);}
            else{adminOverrideActive=false;adminOverrideBtn.setText("Admin: Edit Locked");adminOverrideBtn.setBackground(UITheme.ORANGE);}
            sheetTable.repaint();
        }
        void loadTable(){fillTable(MemberManager.loadAll());clearSheetPanel();}
        void fillTable(List<Member> list){tableModel.setRowCount(0);for(Member m:list)tableModel.addRow(new Object[]{m.studentId,m.name,m.course,m.yearLevel,m.position,m.status,AttendanceManager.attendanceCount(m.studentId)});}
        void refreshSheetPanel(){
            int row=table.getSelectedRow();if(row<0){clearSheetPanel();return;}
            currentMemberId=(String)tableModel.getValueAt(row,0);String memberName=(String)tableModel.getValueAt(row,1);
            List<Event> events=EventManager.loadAll();List<String> attendedEventIds=AttendanceManager.getAttendedEventIds(currentMemberId);
            sheetModel.setRowCount(0);rowLocked.clear();boolean hasCompleted=false;
            for(Event ev:events){boolean completed=ev.isCompleted();boolean attended=attendedEventIds.contains(ev.eventId);
                String statusCell = ev.isCompleted() ? "Completed" : ev.isHappening() ? "Happening" : "Upcoming";
                sheetModel.addRow(new Object[]{ev.name,ev.date,statusCell,attended});rowLocked.add(completed);if(completed)hasCompleted=true;}
            int total=events.size(),cnt=attendedEventIds.size(),pct=total==0?0:(int)Math.round(cnt*100.0/total);
            attendNameLabel.setText(memberName);attendSummaryLabel.setText(cnt+" / "+total+" events ("+pct+"%)");
            saveAttendBtn.setEnabled(true);adminOverrideBtn.setEnabled(hasCompleted);
        }
        void clearSheetPanel(){currentMemberId=null;sheetModel.setRowCount(0);rowLocked.clear();attendNameLabel.setText("Select a member");attendSummaryLabel.setText(" ");saveAttendBtn.setEnabled(false);adminOverrideBtn.setEnabled(false);if(adminOverrideActive)toggleAdminOverride();}
        void saveAttendanceFromSheet(){
            if(currentMemberId==null)return;List<Event> events=EventManager.loadAll();int savedCount=0;
            for(int i=0;i<sheetModel.getRowCount();i++){Boolean checked=(Boolean)sheetModel.getValueAt(i,3);String eventId=events.get(i).eventId;boolean locked=i<rowLocked.size()&&rowLocked.get(i);if(locked&&!adminOverrideActive)continue;
                List<String> presentIds=AttendanceManager.getAttendees(eventId);
                if(Boolean.TRUE.equals(checked)&&!presentIds.contains(currentMemberId)){presentIds.add(currentMemberId);AttendanceManager.saveAttendance(eventId,presentIds);savedCount++;}
                else if(Boolean.FALSE.equals(checked)&&presentIds.contains(currentMemberId)){presentIds.remove(currentMemberId);AttendanceManager.saveAttendance(eventId,presentIds);savedCount++;}}
            refreshSheetPanel();String sid=currentMemberId;loadTable();for(int i=0;i<tableModel.getRowCount();i++)if(sid.equals(tableModel.getValueAt(i,0))){table.setRowSelectionInterval(i,i);break;}
            String msg=savedCount>0?"Attendance saved for "+attendNameLabel.getText()+" ("+savedCount+" change(s)).":"No changes to save.";JOptionPane.showMessageDialog(this,msg,"Attendance Saved",JOptionPane.INFORMATION_MESSAGE);
        }
        void doSearch(){String kw=searchField.getText().trim().toLowerCase(),cat=(String)searchCategory.getSelectedItem(),st=(String)statusFilter.getSelectedItem();List<Member> r=new ArrayList<>();for(Member m:MemberManager.loadAll())if(matchesCat(m,cat,kw)&&("All Status".equals(st)||m.status.equals(st)))r.add(m);fillTable(r);}
        boolean matchesCat(Member m,String cat,String kw){if(kw.isEmpty())return true;switch(cat){case"Student ID":return m.studentId.toLowerCase().contains(kw);case"Name":return m.name.toLowerCase().contains(kw);case"Course":return m.course.toLowerCase().contains(kw);case"Year Level":return m.yearLevel.toLowerCase().contains(kw);case"Position":return m.position.toLowerCase().contains(kw);case"Status":return m.status.toLowerCase().contains(kw);default:return m.studentId.toLowerCase().contains(kw)||m.name.toLowerCase().contains(kw)||m.course.toLowerCase().contains(kw)||m.yearLevel.toLowerCase().contains(kw)||m.position.toLowerCase().contains(kw)||m.status.toLowerCase().contains(kw);}}
        Member getSelectedMember(){int r=table.getSelectedRow();if(r<0)return null;return new Member((String)tableModel.getValueAt(r,0),(String)tableModel.getValueAt(r,1),(String)tableModel.getValueAt(r,2),(String)tableModel.getValueAt(r,3),(String)tableModel.getValueAt(r,4),(String)tableModel.getValueAt(r,5));}
        void openAddDialog(){MemberFormDialog d=new MemberFormDialog((JFrame)SwingUtilities.getWindowAncestor(this),null);d.setVisible(true);if(d.saved)loadTable();}
        void openEditDialog(){Member s=getSelectedMember();if(s==null){warn("Please select a member row first.");return;}MemberFormDialog d=new MemberFormDialog((JFrame)SwingUtilities.getWindowAncestor(this),s);d.setVisible(true);if(d.saved)loadTable();}
        void openDeleteDialog(){Member s=getSelectedMember();if(s==null){warn("Please select a member row first.");return;}if(JOptionPane.showConfirmDialog(this,"Delete "+s.name+"?","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){MemberManager.delete(s.studentId);loadTable();JOptionPane.showMessageDialog(this,"Member deleted.");}}
        void warn(String m){JOptionPane.showMessageDialog(this,m,"Notice",JOptionPane.WARNING_MESSAGE);}
    }

    // ============================================================
    //  MEMBER FORM DIALOG
    // ============================================================
    static class MemberFormDialog extends JDialog {
        boolean saved=false; JTextField idField,nameField; JComboBox<String> courseCombo,yearCombo,posCombo,statusCombo;
        MemberFormDialog(JFrame parent,Member ex){super(parent,ex==null?"Add Member":"Edit Member",true);setSize(440,420);setLocationRelativeTo(parent);buildUI(ex);}
        void buildUI(Member ex){
            setLayout(new BorderLayout(8,8));
            getContentPane().setBackground(UITheme.SURFACE);
            JPanel form=new JPanel(new GridLayout(7,2,8,10));
            form.setBackground(UITheme.SURFACE);
            form.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));
            idField=new JTextField(); idField.setDocument(new NumericOnlyDocument());
            nameField=new JTextField();
            courseCombo=new JComboBox<>(Course.labels()); yearCombo=new JComboBox<>(YearLevel.labels());
            posCombo=new JComboBox<>(MemberPosition.labels()); statusCombo=new JComboBox<>(MemberStatus.labels());
            for(JTextField f:new JTextField[]{idField,nameField}){f.setFont(UITheme.FONT_BODY);f.setBorder(BorderFactory.createCompoundBorder(new UITheme.RoundedBorder(UITheme.BORDER,UITheme.R_INPUT,1),BorderFactory.createEmptyBorder(7,10,7,10)));}
            for(JComboBox<?> cb:new JComboBox<?>[]{courseCombo,yearCombo,posCombo,statusCombo})cb.setFont(UITheme.FONT_BODY);
            if(ex!=null){idField.setText(ex.studentId);idField.setEditable(false);idField.setBackground(UITheme.SURFACE_ALT);nameField.setText(ex.name);courseCombo.setSelectedItem(ex.course);yearCombo.setSelectedItem(ex.yearLevel);posCombo.setSelectedItem(ex.position);statusCombo.setSelectedItem(ex.status);}
            else statusCombo.setSelectedItem(MemberStatus.ACTIVE.getLabel());
            JLabel hint=new JLabel("Full Name (e.g. Juan A. Dela Cruz)");
            hint.setFont(UITheme.FONT_CAPTION);hint.setForeground(UITheme.TEXT_SECONDARY);
            form.removeAll();
            JLabel[] labels={new JLabel("Student ID:"),new JLabel("Full Name:"),new JLabel(""),new JLabel("Course:"),new JLabel("Year Level:"),new JLabel("Position:"),new JLabel("Status:")};
            Component[] fields={idField,nameField,hint,courseCombo,yearCombo,posCombo,statusCombo};
            for(int i=0;i<labels.length;i++){labels[i].setFont(UITheme.FONT_BODY);labels[i].setForeground(UITheme.TEXT_SECONDARY);form.add(labels[i]);form.add(fields[i]);}

            JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,12));
            btns.setBackground(UITheme.SURFACE);
            btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            JButton cancel=UITheme.ghostBtn("Cancel");
            JButton save=ex==null?UITheme.primaryBtn("Add Member"):UITheme.primaryBtn("Update Member");
            btns.add(cancel); btns.add(save);

            add(form,BorderLayout.CENTER); add(btns,BorderLayout.SOUTH);
            cancel.addActionListener(e->safeClose()); save.addActionListener(e->handleSave(ex));
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new java.awt.event.WindowAdapter(){public void windowClosing(java.awt.event.WindowEvent e){safeClose();}});
        }
        void safeClose(){if((!idField.getText().isEmpty()||!nameField.getText().isEmpty())&&!saved)if(JOptionPane.showConfirmDialog(this,"You have unsaved data. Close anyway?","Confirm Close",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;dispose();}
        void handleSave(Member ex){
            String id=idField.getText().trim(),name=nameField.getText().trim(),course=(String)courseCombo.getSelectedItem(),year=(String)yearCombo.getSelectedItem(),pos=(String)posCombo.getSelectedItem(),status=(String)statusCombo.getSelectedItem();
            ValidationResult vId=validateId(id,ex);if(!vId.isValid()){err(vId.getMessage());idField.requestFocus();return;}
            ValidationResult vName=validateName(name,ex);
            if(!vName.isValid()){String msg=vName.getMessage();if(msg.startsWith("WARN:")){if(JOptionPane.showConfirmDialog(this,msg.substring(5),"Duplicate Name",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;}else{err(msg);nameField.requestFocus();return;}}
            Member u=new Member(id,name,course,year,pos,status);
            if(ex==null){MemberManager.add(u);JOptionPane.showMessageDialog(this,"Member added successfully.");}else{MemberManager.edit(ex.studentId,u);JOptionPane.showMessageDialog(this,"Member updated successfully.");}
            saved=true;dispose();
        }
        ValidationResult validateId(String id,Member ex){if(id.isEmpty())return ValidationResult.fail("Student ID is required.");if(!id.matches("\\d+"))return ValidationResult.fail("Student ID must be numbers only.");if(ex==null&&MemberManager.idExists(id))return ValidationResult.fail("Student ID \""+id+"\" already exists.");return ValidationResult.ok();}
        ValidationResult validateName(String name,Member ex){if(name.isEmpty())return ValidationResult.fail("Full Name is required.");if(name.contains(","))return ValidationResult.fail("Name must not contain commas.\nExample: Juan A. Dela Cruz");if(!name.matches("^[A-Za-z\u00C0-\u024F\u00d1\u00f1 .'\\-]+$"))return ValidationResult.fail("Name may only contain letters, spaces, dots, apostrophes, or hyphens.\nExample: Juan A. Dela Cruz");String excl=ex!=null?ex.studentId:"";if(MemberManager.nameExists(name,excl))return ValidationResult.fail("WARN:A member named \""+name+"\" already exists.\nContinue?");return ValidationResult.ok();}
        void err(String m){JOptionPane.showMessageDialog(this,m,"Validation Error",JOptionPane.ERROR_MESSAGE);}
    }

    static class NumericOnlyDocument extends javax.swing.text.PlainDocument {
        @Override public void insertString(int o,String s,javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
            if(s==null)return;StringBuilder d=new StringBuilder();for(char c:s.toCharArray())if(Character.isDigit(c))d.append(c);if(d.length()>0)super.insertString(o,d.toString(),a);
        }
    }

    // ============================================================
    //  EVENTS PANEL
    // ============================================================
    static class EventsPanel extends JPanel {
        JTable happeningTable, upcomingTable, completedTable;
        DefaultTableModel happeningModel, upcomingModel, completedModel;
        JTextField searchField; JComboBox<String> statusFilter;

        static final String[] COLUMNS={"Event ID","Event Name","Date","Type","Venue","Status","Description"};
        static final Color HA_HDR=new Color(230,81,0), HA_ROW=new Color(255,243,224), HA_ALT=new Color(255,234,200);
        static final Color UP_HDR=UITheme.GOOGLE_BLUE, UP_ROW=UITheme.BLUE_LIGHT,   UP_ALT=new Color(210,232,252);
        static final Color CP_HDR=new Color(95,99,104), CP_ROW=new Color(241,243,244), CP_ALT=new Color(232,234,237);

        EventsPanel(){setLayout(new BorderLayout(6,6));setBorder(BorderFactory.createEmptyBorder(12,12,12,12));setBackground(UITheme.BG);buildUI();loadTable();}

        void buildUI(){
            JPanel top = UITheme.toolbarPanel();
            JButton addBtn     = UITheme.primaryBtn("Create Event");
            JButton editBtn    = UITheme.outlinedBtn("Edit Event");
            JButton delBtn     = UITheme.dangerBtn("Delete Event");
            JButton checkInBtn = UITheme.warningBtn("Check-In Mode");
            top.add(addBtn); top.add(editBtn); top.add(delBtn); top.add(checkInBtn);

            // ── UI STATE: Check-In Mode disabled by default; enabled only for Happening events ──
            checkInBtn.setEnabled(false);
            checkInBtn.setToolTipText("Check-in is only available for active (Happening) events");
            checkInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            happeningModel=buildTM(); happeningTable=buildST(happeningModel,HA_HDR,HA_ROW,HA_ALT); hideId(happeningTable);
            upcomingModel =buildTM(); upcomingTable =buildST(upcomingModel, UP_HDR,UP_ROW,UP_ALT); hideId(upcomingTable);
            completedModel=buildTM(); completedTable=buildST(completedModel,CP_HDR,CP_ROW,CP_ALT); hideId(completedTable);

            // ── UI STATE: Sync Check-In Mode button whenever table selection changes ──
            // A helper that reads current selection state and updates the button.
            final Runnable syncCheckInBtn = () -> {
                Event sel = getSelectedEvent();
                boolean canCheckIn = (sel != null && sel.isHappening());
                checkInBtn.setEnabled(canCheckIn);
                checkInBtn.setCursor(Cursor.getPredefinedCursor(
                    canCheckIn ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                checkInBtn.setToolTipText(canCheckIn
                    ? null
                    : "Check-in is only available for active (Happening) events");
            };

            // Cross-table selection exclusivity: selecting in one table clears the others,
            // then re-evaluates the button state.
            happeningTable.getSelectionModel().addListSelectionListener(lse -> {
                if (!lse.getValueIsAdjusting()) {
                    if (happeningTable.getSelectedRow() >= 0) {
                        upcomingTable.clearSelection();
                        completedTable.clearSelection();
                    }
                    syncCheckInBtn.run();
                }
            });
            upcomingTable.getSelectionModel().addListSelectionListener(lse -> {
                if (!lse.getValueIsAdjusting()) {
                    if (upcomingTable.getSelectedRow() >= 0) {
                        happeningTable.clearSelection();
                        completedTable.clearSelection();
                    }
                    syncCheckInBtn.run();
                }
            });
            completedTable.getSelectionModel().addListSelectionListener(lse -> {
                if (!lse.getValueIsAdjusting()) {
                    if (completedTable.getSelectedRow() >= 0) {
                        happeningTable.clearSelection();
                        upcomingTable.clearSelection();
                    }
                    syncCheckInBtn.run();
                }
            });

            JSplitPane bottomSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildSec("Upcoming Events",  UP_HDR, upcomingTable),
                buildSec("Completed Events", CP_HDR, completedTable));
            bottomSplit.setResizeWeight(0.5); bottomSplit.setDividerSize(4);

            JSplitPane sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildSec("Happening Today",  HA_HDR, happeningTable),
                bottomSplit);
            sp.setResizeWeight(0.28); sp.setDividerSize(4);

            JPanel bot=new JPanel(new FlowLayout(FlowLayout.LEFT,8,6));
            bot.setBackground(UITheme.SURFACE);
            bot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(4,8,4,8)));
            searchField=new JTextField(18);
            searchField.setFont(UITheme.FONT_BODY);
            searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER,1,true),BorderFactory.createEmptyBorder(5,10,5,10)));
            statusFilter=new JComboBox<>(prependAll(EventStatus.labels()));
            statusFilter.setFont(UITheme.FONT_BODY);
            JButton srch=UITheme.primaryBtn("Search");
            JButton clr =UITheme.ghostBtn("Show All");
            clr.setForeground(UITheme.GOOGLE_BLUE);
            bot.add(new JLabel("Search:")); bot.add(searchField); bot.add(srch); bot.add(clr);
            bot.add(new JLabel("  Status:")); bot.add(statusFilter);

            JPanel mainCard=new JPanel(new BorderLayout(0,0));
            mainCard.setBackground(UITheme.SURFACE);
            mainCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER,1));
            mainCard.add(top, BorderLayout.NORTH);
            mainCard.add(sp,  BorderLayout.CENTER);
            mainCard.add(bot, BorderLayout.SOUTH);

            add(mainCard, BorderLayout.CENTER);

            addBtn.addActionListener(e->openAddDialog());
            editBtn.addActionListener(e->openEditDialog());
            delBtn.addActionListener(e->openDeleteDialog());
            checkInBtn.addActionListener(e->openCheckIn());
            srch.addActionListener(e->doSearch()); searchField.addActionListener(e->doSearch()); statusFilter.addActionListener(e->doSearch());
            clr.addActionListener(e->{searchField.setText("");statusFilter.setSelectedIndex(0);loadTable();});
        }

        void loadTable() { fillAll(EventManager.loadAll()); }
        void fillAll(List<Event> all){
            happeningModel.setRowCount(0); upcomingModel.setRowCount(0); completedModel.setRowCount(0);
            for(Event e:all){Object[] r={e.eventId,e.name,e.date,e.type,e.venue,e.status,e.description};
                if(e.isCompleted()) completedModel.addRow(r); else if(e.isHappening()) happeningModel.addRow(r); else upcomingModel.addRow(r);}
        }
        void doSearch(){
            String kw=searchField.getText().trim(), f=(String)statusFilter.getSelectedItem();
            List<Event> base=kw.isEmpty()?EventManager.loadAll():EventManager.search(kw);
            if(!"All Status".equals(f)){List<Event> fl=new ArrayList<>();for(Event e:base)if(f.equals(e.status))fl.add(e);fillAll(fl);}else fillAll(base);
        }
        Event getSelectedEvent(){
            int r; DefaultTableModel m;
            if((r=happeningTable.getSelectedRow())>=0) m=happeningModel;
            else if((r=upcomingTable.getSelectedRow())>=0) m=upcomingModel;
            else if((r=completedTable.getSelectedRow())>=0) m=completedModel;
            else return null;
            return new Event((String)m.getValueAt(r,0),(String)m.getValueAt(r,1),(String)m.getValueAt(r,2),(String)m.getValueAt(r,3),(String)m.getValueAt(r,6),(String)m.getValueAt(r,4),(String)m.getValueAt(r,5));
        }

        void openCheckIn() {
            Event sel = getSelectedEvent();
            if (sel == null) {
                warn("Please select an event row first to open Check-In Mode.");
                return;
            }
            if (sel.isUpcoming()) {
                JOptionPane.showMessageDialog(this,
                    "<html>" +
                    "<b>Check-in is not available yet.</b><br><br>" +
                    "Event &nbsp;<b>\"" + sel.name + "\"</b>&nbsp; is still <b>Upcoming</b>.<br><br>" +
                    "Check-in is only allowed when the event status is <b>Happening</b>.<br>" +
                    "The status will automatically change to Happening on <b>" + sel.date + "</b>." +
                    "</html>",
                    "Event Not Started", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean adminOverride = false;
            if (sel.isCompleted()) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "<html><b>'" + sel.name + "' is marked as Completed.</b><br><br>" +
                    "Do you want to open in <b>Admin Override</b> mode?</html>",
                    "Event Completed — Admin Override?",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) return;
                adminOverride = (choice == JOptionPane.YES_OPTION);
            }
            new CheckInDialog((JFrame) SwingUtilities.getWindowAncestor(this), sel, adminOverride)
                .setVisible(true);
        }

        void openAddDialog(){EventFormDialog d=new EventFormDialog((JFrame)SwingUtilities.getWindowAncestor(this),null);d.setVisible(true);if(d.saved)loadTable();}
        void openEditDialog(){Event s=getSelectedEvent();if(s==null){warn("Please select an event row first.");return;}EventFormDialog d=new EventFormDialog((JFrame)SwingUtilities.getWindowAncestor(this),s);d.setVisible(true);if(d.saved)loadTable();}
        void openDeleteDialog(){
            Event s=getSelectedEvent();if(s==null){warn("Please select an event row first.");return;}
            int confirm=JOptionPane.showConfirmDialog(this,"<html>Delete event <b>\""+s.name+"\"</b>?<br><br>⚠ This will also remove all attendance records for this event.</html>","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(confirm==JOptionPane.YES_OPTION){EventManager.delete(s.eventId);loadTable();JOptionPane.showMessageDialog(this,"Event deleted. Attendance records removed.");}
        }
        DefaultTableModel buildTM(){return new DefaultTableModel(COLUMNS,0){public boolean isCellEditable(int r,int c){return false;}};}
        JTable buildST(DefaultTableModel m, Color hdr, Color row, Color alt) {
            JTable t = new JTable(m);
            t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            t.setRowHeight(30);
            t.setFont(UITheme.FONT_TABLE_BODY);
            t.setGridColor(UITheme.DIVIDER);
            t.setShowGrid(true);
            t.setIntercellSpacing(new Dimension(0,0));
            t.setSelectionBackground(UITheme.BLUE_LIGHT);
            t.setSelectionForeground(UITheme.TEXT_PRIMARY);
            t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            UITheme.styleTableHeader(t, hdr);
            @SuppressWarnings("serial") DefaultTableCellRenderer rend = new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(tbl, v, sel, foc, r, c);
                    if (sel) {
                        setBackground(tbl.getSelectionBackground());
                        setForeground(tbl.getSelectionForeground());
                    } else {
                        setBackground(r % 2 == 0 ? row : alt);
                        setForeground(UITheme.TEXT_PRIMARY);
                    }
                    setFont(UITheme.FONT_TABLE_BODY);
                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return this;
                }
            };
            t.setDefaultRenderer(Object.class, rend);
            return t;
        }
        JPanel buildSec(String title, Color hdr, JTable tv) {
            JPanel s = new JPanel(new BorderLayout(0, 0));
            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(hdr);
            titleBar.setOpaque(true);
            JLabel l = new JLabel("  " + title);
            l.setFont(UITheme.FONT_BODY_BOLD);
            l.setForeground(Color.WHITE);
            l.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            titleBar.add(l, BorderLayout.CENTER);
            JScrollPane sp = new JScrollPane(tv);
            sp.setBorder(null);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            s.add(titleBar, BorderLayout.NORTH);
            s.add(sp, BorderLayout.CENTER);
            return s;
        }
        void hideId(JTable t){t.getColumnModel().getColumn(0).setMinWidth(0);t.getColumnModel().getColumn(0).setMaxWidth(0);t.getColumnModel().getColumn(0).setWidth(0);}
        void warn(String m){JOptionPane.showMessageDialog(this,m,"Notice",JOptionPane.WARNING_MESSAGE);}
    }

    // ============================================================
    //  EVENT FORM DIALOG
    // ============================================================
    static class EventFormDialog extends JDialog {
        boolean saved=false;
        JTextField nameField,venueField,dateDisplayField;
        JComboBox<String> typeCombo; JTextArea descArea;
        Calendar selectedDate=Calendar.getInstance();

        EventFormDialog(JFrame parent,Event ex){super(parent,ex==null?"Create Event":"Edit Event",true);setSize(460,390);setLocationRelativeTo(parent);buildUI(ex);}

        void buildUI(Event ex){
            setLayout(new BorderLayout(8,8));
            getContentPane().setBackground(UITheme.SURFACE);
            JPanel form=new JPanel(new GridLayout(5,2,8,10));
            form.setBackground(UITheme.SURFACE);
            form.setBorder(BorderFactory.createEmptyBorder(18,18,8,18));

            nameField=new JTextField(); venueField=new JTextField();
            dateDisplayField=new JTextField(); dateDisplayField.setEditable(false);
            dateDisplayField.setBackground(UITheme.SURFACE_ALT);
            typeCombo=new JComboBox<>(EventType.labels());
            descArea=new JTextArea(3,20); descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
            descArea.setFont(UITheme.FONT_BODY);

            for(JTextField f:new JTextField[]{nameField,venueField,dateDisplayField})
                f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER,1,true),BorderFactory.createEmptyBorder(7,10,7,10)));
            nameField.setFont(UITheme.FONT_BODY); venueField.setFont(UITheme.FONT_BODY);
            typeCombo.setFont(UITheme.FONT_BODY);

            if(ex!=null){nameField.setText(ex.name);venueField.setText(ex.venue);try{selectedDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(ex.date));}catch(Exception ignored){}typeCombo.setSelectedItem(ex.type);descArea.setText(ex.description);}
            refreshDate();

            JButton pick=UITheme.outlinedBtn("📅 Pick Date");
            pick.addActionListener(e->{CalendarPickerDialog c=new CalendarPickerDialog(this,selectedDate);c.setVisible(true);if(c.confirmed){selectedDate=c.chosenDate;refreshDate();}});
            JPanel dr=new JPanel(new BorderLayout(6,0));dr.setBackground(UITheme.SURFACE);dr.add(dateDisplayField,BorderLayout.CENTER);dr.add(pick,BorderLayout.EAST);

            String autoStatus=EventManager.computeAutoStatus(new SimpleDateFormat("yyyy-MM-dd").format(selectedDate.getTime()));
            JLabel statusHint=new JLabel("Auto: "+autoStatus+"  (computed from date)");
            statusHint.setFont(UITheme.FONT_CAPTION);statusHint.setForeground(UITheme.TEXT_SECONDARY);

            String[] lblNames={"Event Name:","Date:","Venue:","Type:","Status:"};
            Component[] flds={nameField,dr,venueField,typeCombo,statusHint};
            for(int i=0;i<lblNames.length;i++){JLabel l=new JLabel(lblNames[i]);l.setFont(UITheme.FONT_BODY);l.setForeground(UITheme.TEXT_SECONDARY);form.add(l);form.add(flds[i]);}

            JPanel descPanel=new JPanel(new BorderLayout(4,4));descPanel.setBackground(UITheme.SURFACE);descPanel.setBorder(BorderFactory.createEmptyBorder(0,18,8,18));
            JLabel descLbl=new JLabel("Description:");descLbl.setFont(UITheme.FONT_BODY);descLbl.setForeground(UITheme.TEXT_SECONDARY);
            JScrollPane descScroll=new JScrollPane(descArea);descScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER,1));
            descPanel.add(descLbl,BorderLayout.NORTH);descPanel.add(descScroll,BorderLayout.CENTER);

            JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,12));btns.setBackground(UITheme.SURFACE);
            btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.DIVIDER));
            JButton cancel=UITheme.ghostBtn("Cancel");
            JButton save=ex==null?UITheme.primaryBtn("Create Event"):UITheme.primaryBtn("Update Event");
            btns.add(cancel);btns.add(save);

            add(form,BorderLayout.NORTH);add(descPanel,BorderLayout.CENTER);add(btns,BorderLayout.SOUTH);
            cancel.addActionListener(e->safeClose());save.addActionListener(e->handleSave(ex));
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);addWindowListener(new java.awt.event.WindowAdapter(){public void windowClosing(java.awt.event.WindowEvent e){safeClose();}});
        }
        void refreshDate(){dateDisplayField.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectedDate.getTime()));}
        void safeClose(){if((!nameField.getText().isEmpty()||!venueField.getText().isEmpty())&&!saved)if(JOptionPane.showConfirmDialog(this,"You have unsaved data. Close anyway?","Confirm Close",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;dispose();}
        void handleSave(Event ex){
            String name=nameField.getText().trim();if(name.isEmpty()){JOptionPane.showMessageDialog(this,"Event Name is required.");return;}
            String excl=ex!=null?ex.eventId:"";if(EventManager.eventNameExists(name,excl)){JOptionPane.showMessageDialog(this,"An event named \""+name+"\" already exists.","Duplicate",JOptionPane.ERROR_MESSAGE);nameField.requestFocus();return;}
            String dateStr=new SimpleDateFormat("yyyy-MM-dd").format(selectedDate.getTime());String autoStatus=EventManager.computeAutoStatus(dateStr);
            String id=ex!=null?ex.eventId:EventManager.generateId();
            Event u=new Event(id,name,dateStr,(String)typeCombo.getSelectedItem(),descArea.getText().trim(),venueField.getText().trim(),autoStatus);
            if(ex==null){EventManager.add(u);JOptionPane.showMessageDialog(this,"Event created.");}else{EventManager.edit(ex.eventId,u);JOptionPane.showMessageDialog(this,"Event updated.");}
            saved=true;dispose();
        }
    }

    // ============================================================
    //  CALENDAR PANEL
    // ============================================================
    static class CalendarPanel extends JPanel {
        Calendar displayCal=Calendar.getInstance(); JLabel monthYearLabel; JPanel gridPanel;
        static final String[] DH={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        CalendarPanel(){setLayout(new BorderLayout());setBorder(BorderFactory.createEmptyBorder(10,10,10,10));setBackground(UITheme.BG);buildUI();}
        void buildUI(){
            JPanel nav=new JPanel(new BorderLayout(8,0));nav.setBackground(new Color(32,33,36));nav.setBorder(BorderFactory.createEmptyBorder(10,14,10,14));
            JButton prev=nb("<"),next=nb(">"),today=nb("Today");
            monthYearLabel=new JLabel("",SwingConstants.CENTER);monthYearLabel.setFont(new Font("SansSerif",Font.BOLD,17));monthYearLabel.setForeground(Color.WHITE);
            JPanel ln=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0));ln.setOpaque(false);ln.add(prev);
            JPanel rn=new JPanel(new FlowLayout(FlowLayout.RIGHT,4,0));rn.setOpaque(false);rn.add(today);rn.add(next);
            nav.add(ln,BorderLayout.WEST);nav.add(monthYearLabel,BorderLayout.CENTER);nav.add(rn,BorderLayout.EAST);
            JPanel hr=new JPanel(new GridLayout(1,7,1,0));hr.setBackground(new Color(55,70,95));
            for(String h:DH){JLabel l=new JLabel(h,SwingConstants.CENTER);l.setFont(UITheme.FONT_CAPTION);l.setForeground(new Color(200,210,225));l.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));hr.add(l);}
            gridPanel=new JPanel(new GridLayout(6,7,1,1));gridPanel.setBackground(UITheme.DIVIDER);
            JPanel topSection=new JPanel(new BorderLayout());topSection.add(nav,BorderLayout.NORTH);topSection.add(hr,BorderLayout.SOUTH);
            add(topSection,BorderLayout.NORTH);add(gridPanel,BorderLayout.CENTER);
            prev.addActionListener(e->{displayCal.add(Calendar.MONTH,-1);buildGrid();});next.addActionListener(e->{displayCal.add(Calendar.MONTH,1);buildGrid();});today.addActionListener(e->{displayCal=Calendar.getInstance();buildGrid();});buildGrid();
        }
        void refresh(){buildGrid();}
        void buildGrid(){
            monthYearLabel.setText(new SimpleDateFormat("MMMM yyyy").format(displayCal.getTime()));
            Map<String,List<Event>> ebd=new HashMap<>();for(Event ev:EventManager.loadAll())ebd.computeIfAbsent(ev.date,k->new ArrayList<>()).add(ev);
            Calendar tod=Calendar.getInstance();int ty=tod.get(Calendar.YEAR),tm=tod.get(Calendar.MONTH),td=tod.get(Calendar.DAY_OF_MONTH);
            int dy=displayCal.get(Calendar.YEAR),dm=displayCal.get(Calendar.MONTH);
            Calendar tmp=(Calendar)displayCal.clone();tmp.set(Calendar.DAY_OF_MONTH,1);int sc=tmp.get(Calendar.DAY_OF_WEEK)-1,dim=tmp.getActualMaximum(Calendar.DAY_OF_MONTH),nr=(int)Math.ceil((sc+dim)/7.0);
            gridPanel.removeAll();gridPanel.setLayout(new GridLayout(nr,7,1,1));
            for(int cell=0;cell<nr*7;cell++){int dn=cell-sc+1;if(dn<1||dn>dim){JPanel b=new JPanel();b.setBackground(UITheme.SURFACE_ALT);gridPanel.add(b);}else{boolean it=dn==td&&dm==tm&&dy==ty;String dk=String.format("%04d-%02d-%02d",dy,dm+1,dn);gridPanel.add(buildCell(dn,it,ebd.getOrDefault(dk,new ArrayList<>()),dk));}}
            gridPanel.revalidate();gridPanel.repaint();
        }
        JPanel buildCell(int day, boolean tod, List<Event> evs, String dk) {
            JPanel c = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    if (tod) {
                        g2.setColor(UITheme.GOOGLE_BLUE);
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                    }
                    g2.dispose();
                }
            };
            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
            c.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
            c.setBackground(tod ? UITheme.BLUE_LIGHT : UITheme.SURFACE);
            c.setOpaque(false);
            JLabel dl = new JLabel(String.valueOf(day));
            dl.setFont(new Font(UITheme.BASE_FONT, tod ? Font.BOLD : Font.PLAIN, 12));
            dl.setForeground(tod ? UITheme.GOOGLE_BLUE : UITheme.TEXT_PRIMARY);
            dl.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.add(dl);
            c.add(Box.createVerticalStrut(4));
            for (int i = 0; i < Math.min(evs.size(), 2); i++) {
                c.add(buildEL(evs.get(i).name, evs.get(i).status));
                c.add(Box.createVerticalStrut(3));
            }
            if (evs.size() > 2) {
                JLabel more = new JLabel("+" + (evs.size()-2) + " more");
                more.setFont(UITheme.FONT_CAPTION); more.setForeground(UITheme.GOOGLE_BLUE);
                more.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(more);
            }
            if (!evs.isEmpty()) {
                c.setCursor(new Cursor(Cursor.HAND_CURSOR));
                c.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) { showDetail(dk, evs); }
                    public void mouseEntered(java.awt.event.MouseEvent e) { c.setBackground(new Color(220,235,255)); c.repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent  e) { c.setBackground(tod ? UITheme.BLUE_LIGHT : UITheme.SURFACE); c.repaint(); }
                });
            }
            return c;
        }
        JLabel buildEL(String name, String status) {
            String d = name.length() > 14 ? name.substring(0, 12) + "…" : name;
            Color bg, fg;
            if (EventStatus.HAPPENING.getLabel().equals(status)) {
                bg = UITheme.EV_HAPPENING_BG;  fg = UITheme.EV_HAPPENING_FG;
            } else if (EventStatus.COMPLETED.getLabel().equals(status)) {
                bg = UITheme.EV_COMPLETED_BG;  fg = UITheme.EV_COMPLETED_FG;
            } else {
                bg = UITheme.EV_UPCOMING_BG;   fg = UITheme.EV_UPCOMING_FG;
            }
            final Color pillBg = bg;
            JLabel l = new JLabel(" " + d + " ") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(pillBg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.R_CHIP, UITheme.R_CHIP);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            l.setFont(new Font(UITheme.BASE_FONT, Font.BOLD, 10));
            l.setForeground(fg);
            l.setOpaque(false);
            l.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }
        static final Color CP_HDR=new Color(95,99,104), HA_HDR=new Color(230,81,0);
        void showDetail(String dk,List<Event> evs){String pd=dk;try{pd=new SimpleDateFormat("MMMM d, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(dk));}catch(Exception ignored){}StringBuilder sb=new StringBuilder("Events on "+pd+":\n\n");for(Event ev:evs){sb.append("  Name  : ").append(ev.name).append("\n");sb.append("  Type  : ").append(ev.type).append("\n");sb.append("  Venue : ").append(ev.venue.isEmpty()?"—":ev.venue).append("\n");sb.append("  Status: ").append(ev.status).append("\n");if(!ev.description.isEmpty())sb.append("  Note  : ").append(ev.description).append("\n");sb.append("\n");}JTextArea a=new JTextArea(sb.toString());a.setEditable(false);a.setFont(UITheme.FONT_MONO);a.setBackground(UITheme.SURFACE_ALT);JScrollPane sp=new JScrollPane(a);sp.setPreferredSize(new Dimension(400,280));JOptionPane.showMessageDialog(this,sp,"Events — "+pd,JOptionPane.INFORMATION_MESSAGE);}
        JButton nb(String t) {
            UITheme.RoundedButton b = new UITheme.RoundedButton(t, new Color(55,70,95), new Color(70,90,115), new Color(40,55,80), Color.WHITE);
            b.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
            return b;
        }
    }

    // ============================================================
    //  CALENDAR PICKER DIALOG
    // ============================================================
    static class CalendarPickerDialog extends JDialog {
        boolean confirmed=false; Calendar chosenDate, displayCal; JPanel daysPanel; JLabel monthYearLabel;
        static final String[] DH={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        CalendarPickerDialog(JDialog parent,Calendar initial){super(parent,"Pick a Date",true);setSize(290,300);setLocationRelativeTo(parent);setResizable(false);displayCal=(Calendar)initial.clone();buildUI();}
        void buildUI(){
            setLayout(new BorderLayout(4,4));getContentPane().setBackground(UITheme.SURFACE);
            JPanel nav=new JPanel(new BorderLayout());nav.setBackground(new Color(32,33,36));nav.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
            JButton prev = new UITheme.RoundedButton("<", new Color(55,70,95), new Color(70,90,115), new Color(40,55,80), Color.WHITE);
            JButton next = new UITheme.RoundedButton(">", new Color(55,70,95), new Color(70,90,115), new Color(40,55,80), Color.WHITE);
            for(JButton b:new JButton[]{prev,next}){b.setBorder(BorderFactory.createEmptyBorder(4,12,4,12));}
            monthYearLabel=new JLabel("",SwingConstants.CENTER);monthYearLabel.setFont(UITheme.FONT_BODY_BOLD);monthYearLabel.setForeground(Color.WHITE);
            nav.add(prev,BorderLayout.WEST);nav.add(monthYearLabel,BorderLayout.CENTER);nav.add(next,BorderLayout.EAST);
            JPanel cp=new JPanel(new BorderLayout());JPanel hr=new JPanel(new GridLayout(1,7,2,2));hr.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));hr.setBackground(UITheme.SURFACE);
            for(String h:DH){JLabel l=new JLabel(h,SwingConstants.CENTER);l.setFont(UITheme.FONT_CAPTION);l.setForeground(UITheme.TEXT_SECONDARY);hr.add(l);}
            daysPanel=new JPanel();daysPanel.setBorder(BorderFactory.createEmptyBorder(2,6,6,6));daysPanel.setBackground(UITheme.SURFACE);
            cp.add(hr,BorderLayout.NORTH);cp.add(daysPanel,BorderLayout.CENTER);
            add(nav,BorderLayout.NORTH);add(cp,BorderLayout.CENTER);
            prev.addActionListener(e->{displayCal.add(Calendar.MONTH,-1);buildDays();});next.addActionListener(e->{displayCal.add(Calendar.MONTH,1);buildDays();});buildDays();
        }
        void buildDays(){
            monthYearLabel.setText(new SimpleDateFormat("MMMM yyyy").format(displayCal.getTime()));daysPanel.removeAll();daysPanel.setLayout(new GridLayout(6,7,2,2));
            Calendar tmp=(Calendar)displayCal.clone();tmp.set(Calendar.DAY_OF_MONTH,1);int sd=tmp.get(Calendar.DAY_OF_WEEK)-1,td=tmp.getActualMaximum(Calendar.DAY_OF_MONTH),yr=displayCal.get(Calendar.YEAR),mo=displayCal.get(Calendar.MONTH);
            for(int i=0;i<sd;i++)daysPanel.add(new JLabel(""));
            for(int d=1;d<=td;d++){
                UITheme.RoundedButton b = new UITheme.RoundedButton(String.valueOf(d), UITheme.SURFACE, UITheme.BLUE_LIGHT, new Color(210,230,255), UITheme.TEXT_PRIMARY);
                b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                final int fd=d;
                b.addActionListener(e->{chosenDate=Calendar.getInstance();chosenDate.set(yr,mo,fd);confirmed=true;dispose();});
                daysPanel.add(b);
            }
            for(int i=0;i<42-(sd+td);i++)daysPanel.add(new JLabel(""));daysPanel.revalidate();daysPanel.repaint();
        }
    }

    // ============================================================
    //  RESTORE BACKUP DIALOG
    // ============================================================
    static class RestoreBackupDialog extends JDialog {
        private final JList<String> backupList; private final DefaultListModel<String> listModel;
        private final JLabel infoLabel; private final File[] backupFiles;
        RestoreBackupDialog(JFrame parent){
            super(parent,"Time Machine — Restore Backup",true);setSize(540,420);setLocationRelativeTo(parent);setResizable(false);
            getContentPane().setBackground(UITheme.SURFACE);
            backupFiles=BackupManager.listBackups();listModel=new DefaultListModel<>();
            for(File f:backupFiles)listModel.addElement(BackupManager.formatBackupLabel(f));
            backupList=new JList<>(listModel);backupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            backupList.setFont(UITheme.FONT_MONO);backupList.setFixedCellHeight(30);
            backupList.setBackground(UITheme.SURFACE);backupList.setSelectionBackground(UITheme.BLUE_LIGHT);backupList.setSelectionForeground(UITheme.TEXT_PRIMARY);
            infoLabel=new JLabel(" ");infoLabel.setFont(UITheme.FONT_CAPTION);infoLabel.setForeground(UITheme.TEXT_SECONDARY);infoLabel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
            backupList.addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&backupList.getSelectedIndex()>=0){File f=backupFiles[backupList.getSelectedIndex()];infoLabel.setText("File: "+f.getName()+"   Size: "+f.length()/1024+" KB");}});
            buildUI();
        }
        void buildUI(){
            setLayout(new BorderLayout(8,8));getRootPane().setBorder(BorderFactory.createEmptyBorder(14,16,14,16));
            JLabel title=new JLabel("Select a backup snapshot to restore:");title.setFont(UITheme.FONT_SUBHEADING);title.setForeground(UITheme.TEXT_PRIMARY);title.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
            if(backupFiles.length==0){JLabel empty=new JLabel("<html><center><br><br><b>No backups found.</b><br><br>Backups are created every time the app starts.<br>Restart to create your first backup.</center></html>",SwingConstants.CENTER);empty.setForeground(UITheme.TEXT_SECONDARY);add(title,BorderLayout.NORTH);add(empty,BorderLayout.CENTER);add(buildBtns(false),BorderLayout.SOUTH);return;}
            JScrollPane sp=new JScrollPane(backupList);sp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.BORDER),"Available Snapshots (newest first)"));
            JPanel center=new JPanel(new BorderLayout(4,4));center.setBackground(UITheme.SURFACE);center.add(sp,BorderLayout.CENTER);center.add(infoLabel,BorderLayout.SOUTH);
            add(title,BorderLayout.NORTH);add(center,BorderLayout.CENTER);add(buildBtns(true),BorderLayout.SOUTH);
        }
        JPanel buildBtns(boolean withRestore){
            JButton restore=UITheme.dangerBtn("⏮  Restore Selected Backup");
            restore.setEnabled(false);
            JButton cancel=UITheme.ghostBtn("Cancel");cancel.setForeground(UITheme.TEXT_SECONDARY);
            if(withRestore)backupList.addListSelectionListener(e->{if(!e.getValueIsAdjusting())restore.setEnabled(backupList.getSelectedIndex()>=0);});
            restore.addActionListener(e->handleRestore());cancel.addActionListener(e->dispose());
            JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,4));p.setBackground(UITheme.SURFACE);p.add(cancel);p.add(restore);return p;
        }
        void handleRestore(){
            int idx=backupList.getSelectedIndex();if(idx<0)return;File chosen=backupFiles[idx];String label=BackupManager.formatBackupLabel(chosen);
            String warn="<html><b>You are about to restore:</b><br><br>&nbsp;&nbsp;"+label+"<br><br><b style='color:red'>⚠ WARNING:</b> This will <b>overwrite</b> your current JSON data.<br>This <b>cannot be undone</b>. Proceed?</html>";
            if(JOptionPane.showConfirmDialog(this,new JLabel(warn),"Confirm Restore",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<Void,Void>(){
                protected Void doInBackground()throws Exception{BackupManager.restoreBackup(chosen);return null;}
                protected void done(){setCursor(Cursor.getDefaultCursor());try{get();JOptionPane.showMessageDialog(RestoreBackupDialog.this,"<html><b>Restore successful!</b><br><br>Data rolled back to:<br>"+label+"<br><br>Please switch tabs to reload data.</html>","Restore Complete",JOptionPane.INFORMATION_MESSAGE);dispose();}catch(Exception ex){LOG.log(Level.SEVERE,"Restore failed",ex);JOptionPane.showMessageDialog(RestoreBackupDialog.this,"Restore failed: "+ex.getMessage(),"Restore Error",JOptionPane.ERROR_MESSAGE);}}
            }.execute();
        }
    }

} // end Studentorgsystem