package com.example.surveyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    EditText display;
    String currentOperator = "";
    double firstValue = 0.0;
    boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        display = findViewById(R.id.calcDisplay);
        GridLayout gridLayout = findViewById(R.id.keysGrid); // Make sure your XML Grid has this ID

        // 1. Loop through all buttons in the GridLayout to set ClickListeners
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                btn.setOnClickListener(v -> onButtonClick(btn.getText().toString()));
            }
        }

        // 2. Temperature Converter Logic (Requirement 14)
        findViewById(R.id.btnConvert).setOnClickListener(v -> {
            String val = display.getText().toString();
            if (!val.isEmpty()) {
                try {
                    double f = (Double.parseDouble(val) * 9 / 5) + 32;
                    display.setText(String.format("%.1f F", f));
                    isNewOp = true; // Reset for next calculation
                } catch (NumberFormatException e) {
                    display.setText("Error");
                }
            }
        });
    }

    private void onButtonClick(String text) {
        switch (text) {
            case "C": // Clear
                display.setText("");
                firstValue = 0.0;
                currentOperator = "";
                isNewOp = true;
                break;

            case "+":
            case "-":
            case "*":
            case "/": // Operators
                String currentText = display.getText().toString();
                if (!currentText.isEmpty()) {
                    firstValue = Double.parseDouble(currentText);
                    currentOperator = text;
                    isNewOp = true;
                }
                break;

            case "=": // Calculate
                if (!currentOperator.isEmpty()) {
                    String secondText = display.getText().toString();
                    if (!secondText.isEmpty()) {
                        double secondValue = Double.parseDouble(secondText);
                        double result = 0.0;
                        switch (currentOperator) {
                            case "+": result = firstValue + secondValue; break;
                            case "-": result = firstValue - secondValue; break;
                            case "*": result = firstValue * secondValue; break;
                            case "/": result = firstValue / secondValue; break;
                        }
                        display.setText(String.valueOf(result));
                        currentOperator = "";
                        isNewOp = true;
                    }
                }
                break;

            default: // Numbers (0-9)
                if (isNewOp) {
                    display.setText(text);
                    isNewOp = false;
                } else {
                    display.append(text);
                }
                break;
        }
    }
}