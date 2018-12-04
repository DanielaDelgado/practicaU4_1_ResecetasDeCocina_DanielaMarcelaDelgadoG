package com.example.danielamarcela.practicau4_1_resecetasdecocina_danielamarceladelgadog;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText identificacion,nombre,ingredientes,preparacion,observaciones;
    Button insertar,consultar,eliminar,actualizar;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identificacion=findViewById(R.id.id);
        nombre=findViewById(R.id.nombre);
        ingredientes=findViewById(R.id.ingredientes);
        preparacion=findViewById(R.id.preparacion);
        observaciones=findViewById(R.id.observaciones);

        insertar=findViewById(R.id.insertar);
        consultar=findViewById(R.id.consultar);
        eliminar=findViewById(R.id.eliminar);
        actualizar=findViewById(R.id.actualizar);

        base = new BaseDatos(this,"primera",null,1);

        insertar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                codigoInsertar();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pedirID(1);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (actualizar.getText().toString().startsWith("CONFIRMAR ACTUALIZACION"))
                {
                    invocarConfirmacionActualizacion();
                }else
                    {
                    pedirID(2);
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pedirID(3);
            }
        });
    }


    private void buscarDato(String idABuscar, int origen)
    {
        try{

            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT *FROM RECETAS WHERE ID="+idABuscar;

            Cursor resultado = tabla.rawQuery(SQL,null);

            if(resultado.moveToFirst())
            {
                if(origen==3)
                {
                    String dato = idABuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3)+"&"+resultado.getString(4);
                    invocarConfirmacionEliminacion(dato);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                ingredientes.setText(resultado.getString(2));
                preparacion.setText(resultado.getString(3));
                observaciones.setText(resultado.getString(4));
                if(origen==2)
                {
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR ACTUALIZACION");
                    identificacion.setEnabled(false);
                }

            }
            else
                {
                Toast.makeText(this,"No se encontro resultado",Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e)
        {
            Toast.makeText(this,"No se pudo realizar busqueda",Toast.LENGTH_LONG).show();
        }
    }



    private void invocarConfirmacionEliminacion(String dato)
    {
        String datos[] = dato.split("&");
        final String id = datos[0];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("¿Deseas eliminar?").setPositiveButton("SI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        eliminarId(id);
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR ",null).show();
    }

    private void eliminarId(String dato)
    {

        try
        {
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "DELETE FROM RECETAS WHERE ID=" + dato;

            tabla.execSQL(SQL);
            tabla.close();

            Toast.makeText(this, "Se elimino la receta", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e)
        {
            Toast.makeText(this, "No se pudo eliminar la receta", Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionActualizacion()
    {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTNATE").setMessage("¿Estas seguro que deseas aplicar cambios?").setPositiveButton("SI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        aplicarActualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }

    private void aplicarActualizar(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL= "UPDATE RECETAS SET NOMBRE='"+
                    nombre.getText().toString()+"', " +
                    "INGREDIENTES='"+ingredientes.getText().toString()+"',"+
                    "PREPARACION='"+preparacion.getText().toString()+"',"+
                    "OBSERVACIONES='"+ingredientes.getText().toString()+
                    "' WHERE ID="+identificacion.getText().toString();


            tabla.execSQL(SQL);
            tabla.close();


            Toast.makeText(this,"Actualizacion exitosa",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e)
        {
            Toast.makeText(this,"No se pudo actualizar",Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void pedirID(final int origen)
    {
        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("Valor entero mayor de 0");
        String mensaje ="Escriba el id a buscar";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        if(origen ==2)
        {
            mensaje ="Ecriba el id a modificar";
        }


        if(origen ==3)
        {
            mensaje ="Escriba el id que desea eliminar";
        }


        alerta.setTitle("atencion").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(pidoID.getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity.this,"DEbes escribir un numero",Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();
    }

    private void codigoInsertar()
    {
        try
        {

            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "INSERT INTO RECETAS VALUES(1,'%2','%3','%4','%5')";
            SQL = SQL.replace("1", identificacion.getText().toString());
            SQL = SQL.replace("%2", nombre.getText().toString());
            SQL = SQL.replace("%3", ingredientes.getText().toString());
            SQL = SQL.replace("%4", preparacion.getText().toString());
            SQL = SQL.replace("%5",observaciones.getText().toString());
            tabla.execSQL(SQL);

            Toast.makeText(this,"Se ha guardado la receta",Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e)
        {
            Toast.makeText(this,"No se pudo guardar la receta",Toast.LENGTH_LONG).show();
        }
    }

    private void habilitarBotonesYLimpiarCampos()
    {
        identificacion.setText("");
        nombre.setText("");
        ingredientes.setText("");
        preparacion.setText("");
        observaciones.setText("");

        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        identificacion.setEnabled(true);
    }
}
