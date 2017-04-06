import processing.core.PApplet;

public class Tile
{

    /* Followed the tutorial for learning and reference - https://py.processing.org/tutorials/pixels/ */
    int tileX, tileY, tileNumber;
    Boolean obstacle; // true means the tile is an obstacle
    PApplet pApplet;

    public Tile(int x, int y, int tileSize, PApplet pApplet)
    {
        this.pApplet = pApplet;
        this.tileNumber = computeTileNumber(x, y, tileSize);

        obstacle = false;
        for (int i=x; i<x+tileSize;i++)
        {
            for (int j=y;j<y+tileSize;j++)
            {
                int curPos = i + j*pApplet.width;
                if (pApplet.brightness(pApplet.pixels[curPos])==0)
                {
                    obstacle = true;
                    break;
                }
            }
        }
    }
/* -------------------------------------------------------------------------------------------------------------------- */

    public int computeTileNumber(int x, int y, int tileSize)
    {
            /* Followed the tutorial for learning and reference - https://py.processing.org/tutorials/pixels/ */

        this.tileX = (int) Math.floor(x/tileSize);
        this.tileY = (int) Math.floor(y/tileSize);

        int tileNumber = this.tileX + ((int) pApplet.width/tileSize)*this.tileY;
/* -------------------------------------------------------------------------------------------------------------------- */

        return  tileNumber;
    }
}