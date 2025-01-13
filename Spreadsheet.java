import java.util.HashMap;
import java.util.Map;

public class Spreadsheet {
    private SCell[][] cells;
    private int width;
    private int height;
    private Map<String, Integer> colMap;

    // Constructor
    public Spreadsheet(int rows, int cols) {
        this.width = cols;
        this.height = rows;
        this.cells = new SCell[rows][cols];
        initializeColumnMap();
    }

    // Initialize column mapping (A → 0, B → 1, ..., Z → 25)
    private void initializeColumnMap() {
        colMap = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            colMap.put(Character.toString((char) ('A' + i)), i);
        }
    }

    // Set a cell at (row, col)
    public void set(int row, int col, SCell cell) {
        if (isValidCell(row, col)) {
            cells[row][col] = cell;
        } else {
            throw new IndexOutOfBoundsException("Cell is out of bounds");
        }
    }

    // Get a cell at (row, col)
    public SCell get(int row, int col) {
        if (isValidCell(row, col)) {
            return cells[row][col];
        }
        throw new IndexOutOfBoundsException("Cell is out of bounds");
    }

    // Check if a cell is valid
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    // Evaluate a cell's value
    public String eval(int row, int col) {
        try {
            SCell cell = get(row, col);
            if (cell.isNumber()) {
                return String.valueOf(cell.getValue());
            } else if (cell.isFormula()) {
                return String.valueOf(cell.computeForm());
            } else if (cell.isText()) {
                return cell.getValue();
            }
        } catch (Exception e) {
            return "ERROR";
        }
        return "ERROR";
    }

    // Evaluate all cells
    public String[][] evalAll() {
        String[][] output = new String[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                output[row][col] = eval(row, col);
            }
        }
        return output;
    }

    // Compute the depth of each cell
    public int[][] depth() {
        int[][] depths = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                depths[row][col] = computeDepth(row, col, new boolean[height][width]);
            }
        }
        return depths;
    }

    // Compute depth for a specific cell
    private int computeDepth(int row, int col, boolean[][] visited) {
        if (visited[row][col]) {
            return -1; // Cycle detected
        }
        SCell cell = get(row, col);
        if (cell.isNumber() || cell.isText()) {
            return 0;
        }
        if (cell.isFormula()) {
            visited[row][col] = true;
            int maxDepth = 0;
            for (String dependency : cell.getDependencies()) {
                int depCol = XCell(dependency);
                int depRow = YCell(dependency);
                int depDepth = computeDepth(depRow, depCol, visited);
                if (depDepth == -1) {
                    return -1; // Cycle detected
                }
                maxDepth = Math.max(maxDepth, depDepth);
            }
            visited[row][col] = false;
            return 1 + maxDepth;
        }
        return 0;
    }

    // Convert column reference (e.g., "A" → 0, "AA" → 26)
    public int XCell(String col) {
        int columnIndex = 0;
        for (int i = 0; i < col.length(); i++) {
            columnIndex = columnIndex * 26 + (col.charAt(i) - 'A' + 1);
        }
        return columnIndex - 1;
    }

    // Convert row reference (e.g., "A1" → row index)
    public int YCell(String cellRef) {
        String rowPart = cellRef.replaceAll("[^0-9]", "");
        return Integer.parseInt(rowPart) - 1;
    }
}
