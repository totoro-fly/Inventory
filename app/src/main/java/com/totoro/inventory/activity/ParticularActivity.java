package com.totoro.inventory.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.totoro.inventory.R;
import com.totoro.inventory.data.ProductContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.bt_subtract)
    ImageView btSubtract;
    @Bind(R.id.bt_add)
    ImageView btAdd;
    private Uri currentUri;
    private Uri uri;
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
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        etName.setOnTouchListener(touchListener);
        etAmount.setOnTouchListener(touchListener);
        etPrice.setOnTouchListener(touchListener);
        etSale.setOnTouchListener(touchListener);
        ivAdd.setOnTouchListener(touchListener);
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
        builder.setMessage(R.string.show_message_save);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNameEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.write_name, Toast.LENGTH_LONG).show();
                } else {
                    save();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.gave_up, new DialogInterface.OnClickListener() {
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
                if (isNameEmpty()) {
                    Toast.makeText(this, R.string.write_name, Toast.LENGTH_LONG).show();
                } else {
                    save();
                    finish();
                }
                break;
            case R.id.delete_data:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ParticularActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(ParticularActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            default:
                break;
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.gave_up, new DialogInterface.OnClickListener() {
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
                Toast.makeText(this, R.string.delete_failure, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.delete_succeed, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void save() {
        String nameStr = etName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String saleStr = etSale.getText().toString().trim();
        ivAdd.setDrawingCacheEnabled(true);
        Bitmap bitmap = ivAdd.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] datas = baos.toByteArray();
        ivAdd.setDrawingCacheEnabled(false);
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameStr);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT, amountStr);
        values.put(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE, priceStr);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE, saleStr);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, datas);
        if (currentUri == null) {
            Uri uri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(this, R.string.insert_failure, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.insert_succeed, Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.nothing_update, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.update_succeed, Toast.LENGTH_LONG).show();
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
                ProductContract.ProductEntry.COLUMN_PRODUCT_SALE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(
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
            int imageIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
            String name = data.getString(nameIndex);
            Integer amount = data.getInt(amountIndex);
            Double price = data.getDouble(priceIndex);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            Integer sale = data.getInt(saleIndex);
            byte[] image = data.getBlob(imageIndex);
            ByteArrayInputStream stream = new ByteArrayInputStream(image);
            etName.setText(name + "");
            etAmount.setText(amount + "");
            etPrice.setText(decimalFormat.format(price) + "");
            etSale.setText(sale + "");
            ivAdd.setImageDrawable(Drawable.createFromStream(stream, "img"));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        etName.setText(R.string.nothing);
        etAmount.setText(R.string.nothing);
        etPrice.setText(R.string.nothing);
        etSale.setText(R.string.nothing);
        ivAdd.setImageResource(R.drawable.ic_add_shopping_cart_black_48dp);
        
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (data == null) {
                return;
            }
            uri = data.getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick({R.id.bt_subtract, R.id.bt_add, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_subtract:
                String saleStrSubtract = etSale.getText().toString().trim();
                int saleIntSubtract = Integer.parseInt(saleStrSubtract);
                saleIntSubtract--;
                etSale.setText(saleIntSubtract + "");
                break;
            case R.id.bt_add:
                String saleStrAdd = etSale.getText().toString().trim();
                int saleIntAdd = Integer.parseInt(saleStrAdd);
                saleIntAdd++;
                etSale.setText(saleIntAdd + "");
                break;
            case R.id.iv_add:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                ivAdd.setImageURI(uri);
                break;
        }
    }

    private boolean isNameEmpty() {
        String nameStr = etName.getText().toString().trim();
        return TextUtils.isEmpty(nameStr);
    }
}
