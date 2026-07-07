package com.example.taller_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SurveyDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Encuestas.db";
    public static final int DATABASE_VERSION = 5;

    public SurveyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SurveyContract.SQL_CREATE_PREGUNTAS);
        db.execSQL(SurveyContract.SQL_CREATE_RESPUESTAS);

        // Las preguntas iniciales se cargan automaticamente al crear la base.
        insertarPreguntasIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + SurveyContract.RespuestasEntry.TABLE_NAME +
                    " ADD COLUMN " + SurveyContract.RespuestasEntry.COLUMN_NOMBRE + " TEXT");
            db.execSQL("ALTER TABLE " + SurveyContract.RespuestasEntry.TABLE_NAME +
                    " ADD COLUMN " + SurveyContract.RespuestasEntry.COLUMN_APELLIDO + " TEXT");
        }

        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + SurveyContract.RespuestasEntry.TABLE_NAME +
                    " ADD COLUMN " + SurveyContract.RespuestasEntry.COLUMN_CORREO + " TEXT");
        }

        // Se limpian las encuestas anteriores cuando cambia el formato del formulario.
        if (oldVersion < 5) {
            db.delete(SurveyContract.RespuestasEntry.TABLE_NAME, null, null);
            db.delete(SurveyContract.PreguntasEntry.TABLE_NAME, null, null);
            insertarPreguntasIniciales(db);
        }

        if (oldVersion > 5) {
            db.execSQL(SurveyContract.SQL_DELETE_RESPUESTAS);
            db.execSQL(SurveyContract.SQL_DELETE_PREGUNTAS);
            onCreate(db);
        }
    }

    private void insertarPreguntasIniciales(SQLiteDatabase db) {
        insertarPregunta(db, 1, "¿Cómo califica la atención recibida?");
        insertarPregunta(db, 2, "¿El personal resolvió su solicitud?");
        insertarPregunta(db, 3, "¿Recomendaría nuestro servicio?");
        insertarPregunta(db, 4, "¿Cómo calificaría el tiempo de respuesta?");
        insertarPregunta(db, 5, "¿La información recibida fue clara?");
        insertarPregunta(db, 6, "¿Cómo calificaría la facilidad para realizar su trámite?");
        insertarPregunta(db, 7, "¿Qué tan satisfecho quedó con el servicio en general?");
        insertarPregunta(db, 8, "¿Qué sugerencia nos dejaría?");
    }

    private void insertarPregunta(SQLiteDatabase db, int idPregunta, String textoPregunta) {
        ContentValues values = new ContentValues();
        values.put(SurveyContract.PreguntasEntry.COLUMN_ID_PREG, idPregunta);
        values.put(SurveyContract.PreguntasEntry.COLUMN_TEXTO, textoPregunta);
        db.insert(SurveyContract.PreguntasEntry.TABLE_NAME, null, values);
    }
}
