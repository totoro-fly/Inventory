package com.totoro.inventory.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.totoro.inventory.R;
import com.totoro.inventory.data.ProductContract;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParticularActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.et_amount)
    EditText etAmount;
    @Bind(R.id.textView3)
    TextView textView3;
    @Bind(R.id.et_price)
    EditText etPrice;
    @Bind(R.id.textView4)
    TextView textView4;
    @Bind(R.id.et_sale)
    EditText etSale;
    private Uri currentUri;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private boolean mProductHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri == null) {
            setTitle("添加货物");
            invalidateOptionsMenu();
        } else {
            setTitle("编辑货物");
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        etName.setOnTouchListener(touchListener);
        etAmount.setOnTouchListener(touchListener);
        etPrice.setOnTouchListener(touchListener);
        etSale.setOnTouchListener(touchListener);
//        Log.d("tag", String.valueOf(currentUri));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_data);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您要保存修改吗？");
        builder.setPositiveButton("放弃", discardButtonClickListener);
        builder.setNegativeButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_particular, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_data:
                save();
                finish();
                break;
            case R.id.delete_data:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ParticularActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ParticularActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除货物");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "删除失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void save() {
        String nameStr = etName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String saleStr = etSale.getText().toString().trim();
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameStr);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT, amountStr);
        values.put(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE, priceStr);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE, saleStr);
        if (currentUri == null) {
            Uri uri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(this, "添加失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "无更新", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "更新成功", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT,
                ProductContract.ProductEntry.COLUME_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SALE
        };
        return new android.content.CursorLoader(
                this,
                currentUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int amountIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT);
            int priceIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE);
            int saleIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE);
            String name = data.getString(nameIndex);
            Integer amount = data.getInt(amountIndex);
            Double price = data.getDouble(priceIndex);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            Integer sale = data.getInt(saleIndex);
            etName.setText(name+"");
            etAmount.setText(amount+"");
            etPrice.setText(decimalFormat.format(price)+"");
            etSale.setText(sale+"");

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        etName.setText("");
        etAmount.setText("");
        etPrice.setText("");
        etSale.setText("");
    }
}
