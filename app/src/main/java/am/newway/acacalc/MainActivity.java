package am.newway.acacalc;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener
{
    private int[] bntCalc = {R.id.bnt0
            , R.id.bnt1
            , R.id.bnt2
            , R.id.bnt3
            , R.id.bnt4
            , R.id.bnt5
            , R.id.bnt6
            , R.id.bnt7
            , R.id.bnt8
            , R.id.bnt9
            , R.id.bntBack
            , R.id.bntDivide
            , R.id.bntMultiply
            , R.id.bntPlus
            , R.id.bntMinus
            , R.id.bntDot
            , R.id.bntEqual
    };

    private TextView textV;
    private Vibrator vibe;

    public MainActivity() {}

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        textV = findViewById( R.id.textNumber );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "lcd.ttf");
        textV.setTypeface(custom_font);

        vibe = (Vibrator) getSystemService( Context.VIBRATOR_SERVICE);

        for(int bnt : bntCalc)
            findViewById( bnt ).setOnClickListener( this );
        int BACKBNT = 10;
        findViewById( bntCalc[BACKBNT] ).setOnLongClickListener( this );
        int MINUSBNT = 14;
        findViewById( bntCalc[MINUSBNT] ).setOnLongClickListener( this );
    }

    public boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }

    public boolean isDot(String str) {
        return ".".equals( str );
    }

    public boolean isSymbol(String str) {
        return ("+".equals( str ) || "-".equals( str ) || "/".equals( str ) || "*".equals( str ));
    }

    private void onAction( String str)
    {
        String strText = textV.getText().toString();

        //Եթե մատեմատիկական նշան է և արդեն կա հնրավոր գործողություն
        //ապա կատարել նոր անցնել հաջորդ քայլին
        if(isSymbol( str ) && isCorrect()){
            checkEqual();
            onAction(str);
            return;
        }

        //Եթե մուտքագրվել է կետ (.) և առկա տեքստը դատարկ է կամ
        //վերջին սիմվոլը մաթեմատիկական գործողություն է ապա կետից առաջ ավելացնել զրո (0)
        if(".".equals( str ) && (strText.isEmpty()
                || isSymbol( String.valueOf( strText.charAt(strText.length() - 1)))))
            str = "0.";

        //Եթե վերջին սիմվոլը մաթեմատիկական գործողություն է և մուտքագրված սիմվոլը մաթեմատիկական գործողություն է
        //ապա հեռացնել առկա վերջին սիմվոլը
        if(isSymbol(String.valueOf( strText.charAt(strText.length() - 1))) && isSymbol( str ))
            strText = strText.substring( 0, strText.length() - 1 );

        //Եթե առկա տեքստի վերջին սիմվոլը և մուտքագրված սիմվոլը կետ է (.) ապա անտեսել մուտքը
        if(".".endsWith( strText ) && isDot( str ))
            return;

        //Եթե առկա տեքստի վերջին սիմվոլը կետ է (.) և մուտքագրված սիմվոլը մաթեմատիկական գործողություն է
        //ապա ջնջել կետը
        if(".".endsWith( strText ) && isSymbol( str ))
            strText = strText.substring( 0, strText.length() - 1 );

        //Եթե առկա տեքստը զրո (0) է և մուտքագրվող սիմվոլը կետ կամ մաթեմաիկական գործողություն չէ ապա
        //փոխարինել առկա սիմվոլը նորով հակառակ դեպքում միացնել իրար
        if("0".equals( strText ) && !isDot( str ) && !isSymbol( str ))
            textV.setText( str );
        else if(strText.length() > 2 && strText.endsWith( "0" )  && isSymbol(String.valueOf( strText.charAt(strText.length() - 2))))
            textV.setText( String.format( "%s%s" , strText.substring( 0 , strText.length() - 1 ) , str ) );
        else textV.setText( String.format( "%s%s" , strText , str ) );
    }

    private void setMinus()
    {
        String strText = textV.getText().toString();
        if(-1 == findMathSymbol(strText))
        {
            strText = strText.startsWith( "-" ) ? strText.substring( 1 ) : "-" + strText;
            textV.setText( strText );
        }
    }

    private boolean isCorrect()
    {
        String strText = textV.getText().toString();
        int nSymbol = findMathSymbol(strText);
        if(-1 != nSymbol )
        {
            String firstValue = strText.substring( 0, nSymbol );
            String lastValue = strText.substring( nSymbol + 1);
            return isNumeric( firstValue ) && isNumeric( lastValue );
        }
        return false;
    }

    private void makeMath(Double firstValue, Double lastValue, MathAction action)
    {
        double dValue;
        switch ( action )
        {
            case DIVIDE:
                dValue = firstValue / lastValue;
                break;
            case MULTIPLY:
                dValue = firstValue * lastValue;
                break;
            case PLUS:
                dValue = firstValue + lastValue;
                break;
            case MINUS:
                dValue = firstValue - lastValue;
                break;
            default:
                return;
        }
        DecimalFormat df = new DecimalFormat( "###.########" );
        textV.setText( df.format( dValue ) );
    }
    
    private int findMathSymbol(String str)
    {
        int nMinus = Boolean.compare(str.startsWith( "-" ), false);
        str = str.startsWith("-") ? str.substring(1) : str;
        return  nMinus + (str.contains( "-" ) ? str.indexOf( "-" ) :
                        str.contains( "+" ) ? str.indexOf( "+" ) :
                                str.contains( "/" ) ? str.indexOf( "/" ) :
                                        str.contains( "*" ) ? str.indexOf( "*" ) : -1);
    }
    
    private void checkEqual()
    {
        String strText = textV.getText().toString();
        int nSymbol = findMathSymbol(strText);

        if(-1 != nSymbol )
        {
            String firstValue = strText.substring( 0, nSymbol );
            String lastValue = strText.substring( nSymbol + 1);
            if(0 == lastValue.length())
                return;

            double firstDouble;
            double lastDouble;

            try{
                firstDouble = Double.valueOf( firstValue );
                lastDouble = Double.valueOf( lastValue );
            }
            catch(NumberFormatException e){
                Toast.makeText( this , "¯\\_(ツ)_/¯" , Toast.LENGTH_SHORT ).show();
                vibe.vibrate(50);
                return;
            }
            makeMath(firstDouble , lastDouble, MathAction.get( String.valueOf(strText.charAt( nSymbol ))));
        }//else Toast.makeText(this, "-1", Toast.LENGTH_LONG).show();
    }

    @Override public void onClick( View v )
    {
        switch (v.getId())
        {
            case R.id.bnt0 :
            case R.id.bnt1 :
            case R.id.bnt2 :
            case R.id.bnt3 :
            case R.id.bnt4 :
            case R.id.bnt5 :
            case R.id.bnt6 :
            case R.id.bnt7 :
            case R.id.bnt8 :
            case R.id.bnt9 :
            case R.id.bntDot :
            case R.id.bntDivide:
            case R.id.bntMultiply:
            case R.id.bntMinus :
            case R.id.bntPlus:
                onAction(((Button)findViewById( v.getId() )).getText().toString());
                break;
            case R.id.bntBack: textV.setText( textV.getText().toString().length() > 1
                    ? textV.getText().toString().substring( 0,  textV.getText().toString().length() - 1)
                    : "0"); break;
            case R.id.bntEqual: checkEqual(); break;
        }
    }

    @Override public boolean onLongClick( View v )
    {
        if (v.getId() == R.id.bntBack)
            textV.setText( "0" );
        if (v.getId() == R.id.bntMinus)
            setMinus();
        return true;
    }
}
