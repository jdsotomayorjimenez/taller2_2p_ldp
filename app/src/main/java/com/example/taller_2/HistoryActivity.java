package com.example.taller_2;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryActivity extends Activity {

    private SurveyDbHelper dbHelper;
    private ListView listHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new SurveyDbHelper(this);
        listHistorial = findViewById(R.id.listHistorial);
        Button btnVolver = findViewById(R.id.btnVolver);

        cargarHistorial();

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarHistorial() {
        ArrayList<CharSequence> encuestas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT r." + SurveyContract.RespuestasEntry.COLUMN_FECHA + ", " +
                "r." + SurveyContract.RespuestasEntry.COLUMN_NOMBRE + ", " +
                "r." + SurveyContract.RespuestasEntry.COLUMN_APELLIDO + ", " +
                "r." + SurveyContract.RespuestasEntry.COLUMN_CORREO + ", " +
                "p." + SurveyContract.PreguntasEntry.COLUMN_TEXTO + ", " +
                "r." + SurveyContract.RespuestasEntry.COLUMN_RESPUESTA + " " +
                "FROM " + SurveyContract.RespuestasEntry.TABLE_NAME + " r " +
                "INNER JOIN " + SurveyContract.PreguntasEntry.TABLE_NAME + " p " +
                "ON r." + SurveyContract.RespuestasEntry.COLUMN_ID_PREG_FK + " = " +
                "p." + SurveyContract.PreguntasEntry.COLUMN_ID_PREG + " " +
                "ORDER BY r." + SurveyContract.RespuestasEntry.COLUMN_FECHA + " DESC, " +
                "p." + SurveyContract.PreguntasEntry.COLUMN_ID_PREG + " ASC";

        Cursor cursor = db.rawQuery(sql, null);

        try {
            String fechaActual = "";
            SpannableStringBuilder encuestaActual = new SpannableStringBuilder();

            while (cursor.moveToNext()) {
                String fecha = cursor.getString(0);
                String nombre = cursor.getString(1);
                String apellido = cursor.getString(2);
                String correo = cursor.getString(3);
                String pregunta = cursor.getString(4);
                String respuesta = cursor.getString(5);

                if (!fecha.equals(fechaActual)) {
                    if (encuestaActual.length() > 0) {
                        encuestas.add(encuestaActual);
                    }

                    fechaActual = fecha;
                    encuestaActual = new SpannableStringBuilder();
                    agregarLineaNegrita(encuestaActual, "Fecha: ", fechaActual);

                    if ((nombre != null && !nombre.trim().isEmpty()) ||
                            (apellido != null && !apellido.trim().isEmpty())) {
                        agregarLineaNegrita(encuestaActual, "Cliente: ",
                                (nombre == null ? "" : nombre) + " " +
                                        (apellido == null ? "" : apellido));
                    }

                    if (correo != null && !correo.trim().isEmpty()) {
                        agregarLineaNegrita(encuestaActual, "Correo: ", correo);
                    }
                }

                encuestaActual.append("\n");
                agregarPreguntaRespuesta(encuestaActual, pregunta, respuesta);
            }

            if (encuestaActual.length() > 0) {
                encuestas.add(encuestaActual);
            }
        } finally {
            cursor.close();
        }

        if (encuestas.isEmpty()) {
            Toast.makeText(this, "No hay encuestas guardadas", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_historial,
                encuestas
        );
        listHistorial.setAdapter(adapter);
    }

    private void agregarLineaNegrita(SpannableStringBuilder builder, String etiqueta, String valor) {
        if (builder.length() > 0) {
            builder.append("\n");
        }

        int inicio = builder.length();
        builder.append(etiqueta);
        builder.setSpan(new StyleSpan(Typeface.BOLD), inicio, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(valor == null ? "" : valor.trim());
    }

    private void agregarPreguntaRespuesta(SpannableStringBuilder builder, String pregunta, String respuesta) {
        int inicio = builder.length();
        builder.append(pregunta == null ? "" : pregunta);
        builder.setSpan(new StyleSpan(Typeface.BOLD), inicio, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("\nRespuesta: ");
        builder.append(respuesta == null ? "" : respuesta);
        builder.append("\n");
    }
}
