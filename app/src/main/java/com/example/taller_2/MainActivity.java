package com.example.taller_2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {

    private SurveyDbHelper dbHelper;
    private LinearLayout layoutPreguntas;
    private EditText edtNombre;
    private EditText edtApellido;
    private EditText edtCorreo;
    private LinkedHashMap<Integer, View> respuestasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new SurveyDbHelper(this);
        layoutPreguntas = findViewById(R.id.layoutPreguntas);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtCorreo = findViewById(R.id.edtCorreo);
        respuestasView = new LinkedHashMap<>();

        Button btnGuardar = findViewById(R.id.btnGuardarEncuesta);
        Button btnHistorial = findViewById(R.id.btnVerHistorial);

        cargarPreguntas();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarEncuesta();
            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cargarPreguntas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                SurveyContract.PreguntasEntry.COLUMN_ID_PREG,
                SurveyContract.PreguntasEntry.COLUMN_TEXTO
        };

        Cursor cursor = db.query(
                SurveyContract.PreguntasEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                SurveyContract.PreguntasEntry.COLUMN_ID_PREG + " ASC"
        );

        try {
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No hay preguntas registradas", Toast.LENGTH_LONG).show();
                return;
            }

            while (cursor.moveToNext()) {
                int idPregunta = cursor.getInt(cursor.getColumnIndexOrThrow(
                        SurveyContract.PreguntasEntry.COLUMN_ID_PREG));
                String textoPregunta = cursor.getString(cursor.getColumnIndexOrThrow(
                        SurveyContract.PreguntasEntry.COLUMN_TEXTO));

                agregarPreguntaALaInterfaz(idPregunta, textoPregunta);
            }
        } finally {
            cursor.close();
        }
    }

    private void agregarPreguntaALaInterfaz(int idPregunta, String textoPregunta) {
        int colorTexto = getResources().getColor(R.color.survey_text);
        int numeroPregunta = respuestasView.size() + 1;

        TextView txtNumeroPregunta = new TextView(this);
        txtNumeroPregunta.setText("Pregunta " + numeroPregunta);
        txtNumeroPregunta.setTextColor(getResources().getColor(R.color.white));
        txtNumeroPregunta.setTextSize(13);
        txtNumeroPregunta.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        txtNumeroPregunta.setBackgroundResource(R.drawable.bg_question_badge);

        TextView txtPregunta = new TextView(this);
        txtPregunta.setText(textoPregunta);
        txtPregunta.setTextColor(colorTexto);
        txtPregunta.setTextSize(15);
        txtPregunta.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        txtPregunta.setPadding(0, 8, 0, 6);

        LinearLayout.LayoutParams numeroParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numeroParams.setMargins(0, 22, 0, 0);

        LinearLayout.LayoutParams preguntaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams respuestaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        respuestaParams.setMargins(0, 2, 0, 16);

        txtNumeroPregunta.setLayoutParams(numeroParams);
        txtPregunta.setLayoutParams(preguntaParams);

        layoutPreguntas.addView(txtNumeroPregunta);
        layoutPreguntas.addView(txtPregunta);

        View campoRespuesta;
        if (idPregunta == 1) {
            campoRespuesta = crearOpciones(new String[]{"Excelente", "Buena", "Regular", "Mala", "Muy mala"});
        } else if (idPregunta == 2) {
            campoRespuesta = crearOpciones(new String[]{"Sí", "No"});
        } else if (idPregunta == 3) {
            campoRespuesta = crearOpciones(new String[]{"Sí", "Tal vez", "No estoy seguro", "No"});
        } else if (idPregunta == 4) {
            campoRespuesta = crearOpciones(new String[]{"Muy rápido", "Rápido", "Normal", "Lento", "Muy lento"});
        } else if (idPregunta == 5) {
            campoRespuesta = crearOpciones(new String[]{"Muy clara", "Clara", "Regular", "Poco clara", "Nada clara"});
        } else if (idPregunta == 6) {
            campoRespuesta = crearOpciones(new String[]{"Muy fácil", "Fácil", "Normal", "Difícil", "Muy difícil"});
        } else if (idPregunta == 7) {
            campoRespuesta = crearOpciones(new String[]{"Muy satisfecho", "Satisfecho", "Neutral", "Insatisfecho", "Muy insatisfecho"});
        } else {
            campoRespuesta = crearCampoTexto();
        }

        campoRespuesta.setLayoutParams(respuestaParams);
        layoutPreguntas.addView(campoRespuesta);
        respuestasView.put(idPregunta, campoRespuesta);
    }

    private RadioGroup crearOpciones(String[] opciones) {
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setBackgroundResource(R.drawable.bg_input_box);
        radioGroup.setPadding(dp(8), dp(4), dp(8), dp(4));

        ColorStateList colorOpciones = ColorStateList.valueOf(getResources().getColor(R.color.survey_primary));

        for (String opcion : opciones) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(opcion);
            radioButton.setTextColor(getResources().getColor(R.color.survey_text));
            radioButton.setTextSize(15);
            radioButton.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            radioButton.setButtonTintList(colorOpciones);
            radioGroup.addView(radioButton);
        }

        return radioGroup;
    }

    private EditText crearCampoTexto() {
        EditText edtRespuesta = new EditText(this);
        edtRespuesta.setHint("Escriba su respuesta");
        edtRespuesta.setHintTextColor(getResources().getColor(R.color.survey_text_secondary));
        edtRespuesta.setTextColor(getResources().getColor(R.color.survey_text));
        edtRespuesta.setTextSize(15);
        edtRespuesta.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        edtRespuesta.setBackgroundResource(R.drawable.bg_input_box);
        edtRespuesta.setSingleLine(false);
        edtRespuesta.setMinLines(1);
        edtRespuesta.setMaxLines(4);
        edtRespuesta.setMinHeight(dp(46));
        return edtRespuesta;
    }

    private void guardarEncuesta() {
        String nombre = edtNombre.getText().toString().trim();
        String apellido = edtApellido.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Debe ingresar nombre, apellido y correo", Toast.LENGTH_LONG).show();
            return;
        }

        if (respuestasView.isEmpty()) {
            Toast.makeText(this, "No hay preguntas para guardar", Toast.LENGTH_LONG).show();
            return;
        }

        for (View view : respuestasView.values()) {
            if (obtenerRespuesta(view).isEmpty()) {
                Toast.makeText(this, "Debe responder todas las preguntas", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String fechaEncuesta = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (Map.Entry<Integer, View> entry : respuestasView.entrySet()) {
                ContentValues values = new ContentValues();
                values.put(SurveyContract.RespuestasEntry.COLUMN_ID_PREG_FK, entry.getKey());
                values.put(SurveyContract.RespuestasEntry.COLUMN_RESPUESTA, obtenerRespuesta(entry.getValue()));
                values.put(SurveyContract.RespuestasEntry.COLUMN_FECHA, fechaEncuesta);
                values.put(SurveyContract.RespuestasEntry.COLUMN_NOMBRE, nombre);
                values.put(SurveyContract.RespuestasEntry.COLUMN_APELLIDO, apellido);
                values.put(SurveyContract.RespuestasEntry.COLUMN_CORREO, correo);

                db.insert(SurveyContract.RespuestasEntry.TABLE_NAME, null, values);
            }

            db.setTransactionSuccessful();
            limpiarCampos();
            Toast.makeText(this, "Encuesta guardada correctamente", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    private void limpiarCampos() {
        edtNombre.setText("");
        edtApellido.setText("");
        edtCorreo.setText("");

        for (View view : respuestasView.values()) {
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            } else if (view instanceof RadioGroup) {
                ((RadioGroup) view).clearCheck();
            }
        }
    }

    private String obtenerRespuesta(View view) {
        if (view instanceof EditText) {
            return ((EditText) view).getText().toString().trim();
        }

        if (view instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) view;
            int idSeleccionado = radioGroup.getCheckedRadioButtonId();

            if (idSeleccionado == -1) {
                return "";
            }

            RadioButton radioButton = radioGroup.findViewById(idSeleccionado);
            return radioButton.getText().toString();
        }

        return "";
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
