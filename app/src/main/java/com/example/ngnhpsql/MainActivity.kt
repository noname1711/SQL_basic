package com.example.ngnhpsql

import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ngnhpsql.databinding.ActivityMainBinding
import java.text.SimpleDateFormat

private lateinit var binding : ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //khai bao cac bien
    lateinit var db: SQLiteDatabase
    lateinit var rs: Cursor
    lateinit var adapter: SimpleCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val helper = MyHelper(application)
        db= helper.readableDatabase  //chế độ đọc
        rs = db.rawQuery("select * from tuhoc limit 20",null)  //tương tác lệnh với database
        //truy vấn khoảng 20 dòng

        // do 1 trong db là user , 2 là email (đã viết trên MyHelper)

        //nút first
        binding.btnFirst.setOnClickListener{
            if (rs.moveToFirst()){ //con trỏ nhảy tới dòng đầu tiên
                binding.edtUser.setText(rs.getString(1))
                binding.edtEmail.setText(rs.getString(2))
            }else  //nếu không có dòng data nào
                Toast.makeText(application,"không có data",Toast.LENGTH_SHORT).show()
        }
        //nút next
        binding.btnNext.setOnClickListener {
            if (rs.moveToNext()){  //con trỏ nhảy sang dòng tiếp theo
                binding.edtUser.setText(rs.getString(1))
                binding.edtEmail.setText(rs.getString(2))
            }
            //để khi next đến data cuối cùng thì nó lại lộn lại đến data đầu tiên, tạo thành vòng lặp vĩnh cửu
            else if (rs.moveToFirst()){
                binding.edtUser.setText(rs.getString(1))
                binding.edtEmail.setText(rs.getString(2))
            }  //nếu ko có đoạn else if này thì khi đến cuối thì sẽ ko có data
            else  //nếu không có data
                Toast.makeText(application,"không có data",Toast.LENGTH_SHORT).show()
        }
        //nút prev
        binding.btnPrev.setOnClickListener{
            if (rs.moveToPrevious()){   //con trỏ nhảy sang dòng trước đó
                binding.edtUser.setText(rs.getString(1))
                binding.edtEmail.setText(rs.getString(2))
            }
            //để khi prev đến data đầu tiên thì nó lại lộn lại đến data cuối cùng, tạo thành vòng lặp vĩnh cửu
            else if (rs.moveToLast()){
                binding.edtUser.setText(rs.getString(1))
                binding.edtEmail.setText(rs.getString(2))
            }  //nếu ko có đoạn else if này thì khi đến đầu tiên thì sẽ ko có data
            else   //nếu không có data
                Toast.makeText(application,"không có data",Toast.LENGTH_SHORT).show()
        }
        //nút last
        binding.btnLast.setOnClickListener {
            if(rs.moveToLast()){   //con trỏ nhảy tới dòng cuối
                binding.edtUser.setText(rs.getString(1))
                binding.edtEmail.setText(rs.getString(2))
            }
            else   //nếu ko có data
                Toast.makeText(application,"không có data",Toast.LENGTH_SHORT).show()
        }

        //code list view
        adapter = SimpleCursorAdapter(
            applicationContext,android.R.layout.simple_expandable_list_item_2,
            rs,
            arrayOf("user","email"),intArrayOf(android.R.id.text1,android.R.id.text2),
            0)
            //layout hiển thị mặc định của list view, sử dụng 2 để hiên thị cả 2 dòng data nhập vào từ username và email
            //con trỏ đang dùng là rs
            //đưa vào mảng string là 2 cột user và email, là cột mà cần lấy dữ liệu
            //flags: cờ để = 0,Cờ được sử dụng để xác định hành vi của bộ điều hợp(adpater)
        binding.lvFull.adapter = adapter
        //code cho nút view all
        binding.btnViewAll.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE    //hiển thị search view lên
            binding.lvFull.visibility = View.VISIBLE       //hiển thị lvFull lên
            adapter.notifyDataSetChanged()   //thông báo list view đã thay đổi dữ liệu để cập nhật lại
            binding.searchView.queryHint = "Tìm kiếm trong ${rs.count} data"
            //rs.count để đếm số lượng data
            //queryHint để hiển thị gợi ý mờ mờ cho search view

            //chức năng tìm kiếm
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                //ctrl + i
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //onQueryTextSubmit để gửi truy vấn nhưng ở đây ko dùng nút bấm nào nên cho về false
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    rs = db.rawQuery("select * from tuhoc where user like '%${newText}' or email like '%${newText}'",null)
                    //truy vấn user hoặc email có giống như dữ liệu người dùng nhập không
                    //null là ko truy vấn theo điều kiện nào
                    adapter.changeCursor(rs)
                    return true
                }
            })


            //nút Insert
            binding.btnInsert.setOnClickListener{
                val cv = ContentValues()    //contentvalues để truyền dữ liệu
                //người dùng truyền dữ liệu vào cột user và email
                cv.put("user",binding.edtUser.text.toString())
                cv.put("email",binding.edtEmail.text.toString())
                db.insert("tuhoc",null,cv)
                rs.requery()   //update lại database
                adapter.notifyDataSetChanged()   // cho biết data đã thay đổi để update lại database
                binding.searchView.queryHint = "Tìm kiếm trong ${rs.count} data"   //update lại search view
                //hiển thị thông báo
                val ad = AlertDialog.Builder(this)
                ad.setTitle("Add data")
                ad.setMessage("thành công")
                ad.setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    //đưa 2 ô nhập liệu về rỗng
                    binding.edtUser.setText("")
                    binding.edtEmail.setText("")
                    binding.edtUser.requestFocus()  //chuyển con trỏ nhập liệu về ô user
                })
                ad.show()   //hiển thị hộp thoại alert dialog
            }

            //nút update
            binding.btnUpdate.setOnClickListener{
                val cv = ContentValues()    //contentvalues để truyền dữ liệu
                //người dùng truyền dữ liệu vào cột user và email
                cv.put("user",binding.edtUser.text.toString())
                cv.put("email",binding.edtEmail.text.toString())
                db.update("tuhoc",cv,"_id=?",arrayOf(rs.getString(0)))
                //bảng, đối tượng muốn chỉnh sửa, lọc theo cột _id, tập các giá trị của điều kiện lọc(ở đây là theo cột 0 là cột _id
                rs.requery()   //update lại database
                adapter.notifyDataSetChanged()   // cho biết data đã thay đổi để update lại database
                //hiển thị thông báo
                val ad = AlertDialog.Builder(this)
                ad.setTitle("Update data")
                ad.setMessage("thành công")
                ad.setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    //đưa 2 ô nhập liệu về rỗng
                    binding.edtUser.setText("")
                    binding.edtEmail.setText("")
                    binding.edtUser.requestFocus()  //chuyển con trỏ nhập liệu về ô user
                })
                ad.show()   //hiển thị hộp thoại alert dialog
            }

            //nút clear
            binding.btnClear.setOnClickListener{
                binding.edtUser.setText("")
                binding.edtEmail.setText("")
                binding.edtUser.requestFocus()
            }

            //nút delete
            binding.btnDelete.setOnClickListener{
                db.delete("tuhoc","_id=?",arrayOf(rs.getString(0)))
                rs.requery()    //update lại database
                adapter.notifyDataSetChanged()   //tự update lại luôn
                binding.searchView.queryHint = "Tìm kiếm trong ${rs.count} data"   //update lại search view
                //thông báo
                val ad = AlertDialog.Builder(this)
                ad.setTitle("Xóa data")
                ad.setMessage("thành công")
                ad.setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    if (rs.moveToFirst()) {
                        //đưa 2 ô nhập liệu về rỗng
                        binding.edtUser.setText("")
                        binding.edtEmail.setText("")
                        binding.edtUser.requestFocus()  //chuyển con trỏ nhập liệu về ô user
                    }else{    //nếu đã xóa hết data
                        binding.edtUser.setText("ko có data")
                        binding.edtEmail.setText("ko có data")
                    }
                })
                ad.show()   //hiển thị hộp thoại alert dialog
            }



            //đăng kí sử dụng context menu cho list view
            registerForContextMenu(binding.lvFull)
        }

    }
    //ctrl + o
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(1,11,1,"Xóa data")
        menu?.setHeaderTitle("Delete")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId==11){
            db.delete("tuhoc","_id=?",arrayOf(rs.getString(0)))
            rs.requery()    //update lại database
            adapter.notifyDataSetChanged()     //reset lại data
            binding.searchView.queryHint = "Tìm kiếm trong ${rs.count} data"   //update lại search view
        }
        return super.onContextItemSelected(item)
    }
}