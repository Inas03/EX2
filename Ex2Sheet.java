
import java.io.*;
import java.util.Map;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    // Add your code here

    // ///////////////////
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        // Add your code here

        Cell c = get(x,y);
        if(c!=null) {ans = c.toString();}

        /////////////////////
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        // Add your code here

        /////////////////////
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
        // Add your code here

        /////////////////////
    }
    @Override
    public void eval() {
        int[][] dd = depth();
        // Add your code here

        // ///////////////////
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        // Add your code here

        /////////////////////
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        // Add your code here
        boolean[][] visited = new boolean[width()][height()];

        for (int x = 0; x <width() ; x++) {
            for (int y = 0; y < height(); y++) {
                ans[x][y] = computeDepth(x,y,visited,new boolean[width()][height()]);


            }

        }

        // ///////////////////
        return ans;
    }
    private int computeDepth(int x, int y , boolean[][] visited , boolean[][] currentPath){
        if (!isIn(x,y)){
            return Ex2Utils.ERR;
        }
        if (visited[x][y]){
            return 0;
        }
        Cell cell= get(x,y);
        if (cell == null || cell.getType()== Ex2Utils.TEXT || cell.getType()==Ex2Utils.NUMBER){
            return 0;
        }
        if (currentPath[x][y]){
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR;
        }
        currentPath[x][y] =true;
        visited[x][y]=true;
        int maxDepth = 0;
        for (String dependency : ((SCell) cell).getDependencies()){
            int depx= Ex2Utils.ABC[dependency.charAt(0)-'A'];
            int depy = Integer.parseInt(dependency.substring(1))-1;
            int depDepth = computeDepth(depx,depy,visited,currentPath);
            if (depDepth ==Ex2Utils.ERR){
                cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                return Ex2Utils.ERR;
            }
            maxDepth = Math.max(maxDepth, depDepth);

        }
        currentPath[x][y]= false;
        return 1 + maxDepth;
    }

    @Override
    public void load(String fileName) throws IOException {
        // Add your code here
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            clearSheet();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("I2cs")) continue;
                String[] parts = line.split((",", 3));
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        String data = parts[2].trim();
                        set(x, y, data);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        /////////////////////
    }
    private void clearSheet(){
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                set(x,y, Ex2Utils.EMPTY_CELL);

            }

        }
    }


    @Override
    public void save(String fileName) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new BufferedWriter(fileName))){
            writer.write("\"I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored\\n\"");
            for (int x = 0; x <width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = get(x,y);
                    if (cell != null && !cell.getData().isEmpty()){
                        writer.write(x+", " + y + ", " +cell.getData()+"\n");

                    }

                }

            }
        }
        // Add your code here

        /////////////////////
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if(get(x,y)!=null) {ans = get(x,y).toString();}
        // Add your code here

        /////////////////////
        return ans;
    }
}
