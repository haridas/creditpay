package in.haridas.creditpay.card;

/**
 * Created by haridas on 8/4/16.
 */
public class Card {

    /**
     * Id of the card, we won't consider the default value of the id.
     */
    int _id = -1;

    /**
     * Statement date between 1-28.
     */
    int statementDay = 0;

    // Number of days after statement days
    int gracePeriod = 0;
    String name = null;

    /**
     * A score attached to this card in the runtime, this will be
     * used to sort the list of card via we pick a card which has
     * good card for today.
     */
    float score = 0;

    public Card() {
    }

    public Card(String name, int statementDay, int gracePeriod) {
        this.name = name;
        this.statementDay = statementDay;
        this.gracePeriod = gracePeriod;
    }

    public Card(int id, String name, int statementDay, int gracePeriod) {
        this._id = id;
        this.name = name;
        this.statementDay = statementDay;
        this.gracePeriod = gracePeriod;
    }

    public int getStatementDay() {
        return statementDay;
    }

    public void setStatementDay(int statementDay) {
        this.statementDay = statementDay;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    /**
     * Helper method for Cursor, to add new card into cursor.
     *
     * @return String[]
     */
    public Object[] getDbRow() {
        Object[] columns = {_id, name, statementDay, gracePeriod };
        return columns;
    }
}
