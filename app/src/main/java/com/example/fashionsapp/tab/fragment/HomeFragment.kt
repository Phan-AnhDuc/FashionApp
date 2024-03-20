import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionsapp.DetailActivity
import com.example.fashionsapp.adapter.AdapterFashion
import com.example.fashionsapp.adapter.AdapterSale
import com.example.fashionsapp.data.ItemFashion
import com.example.fashionsapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var saleAdapter: AdapterSale
    private lateinit var fashionAdapter: AdapterFashion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saleAdapter = AdapterSale()
        fashionAdapter = AdapterFashion()

        setupViewsHorizontal()
        setupViewsVertical()

        fashionAdapter.setOnItemClickListener { fashion ->
            goToDetail(fashion)
        }

        setTransparentStatusBar()
    }

    private fun goToDetail(fashion: ItemFashion) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("fashion", fashion)
        startActivity(intent)
    }

    private fun setupViewsHorizontal() {
        binding?.recyclerviewHorizontal?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = saleAdapter
        }
    }

    private fun setupViewsVertical() {
        binding?.recyclerviewVertical?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = fashionAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTransparentStatusBar() {
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }
}
