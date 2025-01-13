package com.lumko.teachme.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragSettingsFinancialBinding
import com.lumko.teachme.manager.*
import com.lumko.teachme.manager.Utils.toBoolean
import com.lumko.teachme.manager.adapter.ItemSpinnerAdapter
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.SettingsFinancialPresenterImpl
import com.lumko.teachme.ui.widget.LoadingDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

enum class FileType {
    ID_SCAN,
    CERTIFICATION,
    CV,
    PROOF_OF_ADDRESS,
    BANK_CONFIRMATION
}

class SettingsFinancialFrag : Fragment(), View.OnClickListener, SettingsFrag.SaveCallback {

    private lateinit var mBinding: FragSettingsFinancialBinding
    private lateinit var mPresenter: Presenter.SettingsFinancialPresenter
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<String>
    private lateinit var mTakePicActivityResultLauncher: ActivityResultLauncher<Uri>
    private lateinit var mLoadingDialog: LoadingDialog

    private var mCertFile: File? = null
    private var mIdScanFile: File? = null
    private var mCvFile: File? = null
    private var mProofOfAddressFile: File? = null
    private var mBankConfirmationFile: File? = null

    private var mIdScanCameraPermission = false
    private var mIdScanStoragePermission = false

    private var mCertCameraPermission = false
    private var mCertStoragePermission = false

    private var mCvCameraPermission = false
    private var mCvStoragePermission = false

    private var mProofOfAddressCameraPermission = false
    private var mProofOfAddressStoragePermission = false

    private var mBankConfirmationCameraPermission = false
    private var mBankConfirmationStoragePermission = false

    private var mFilesSaved = false
    private var mSettingsSaved = false

    private var currentFileType: FileType? = null
    private lateinit var currentFile: File

    private fun setAllSelectionToFalse() {

        mIdScanCameraPermission = false
        mCertCameraPermission = false
        mCvCameraPermission = false
        mProofOfAddressCameraPermission = false
        mBankConfirmationCameraPermission = false

        mIdScanStoragePermission = false
        mCertStoragePermission = false
        mCvStoragePermission = false
        mProofOfAddressStoragePermission = false
        mBankConfirmationStoragePermission = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    mIdScanCameraPermission && permissions[Manifest.permission.CAMERA] == true -> takePic(FileType.ID_SCAN)
                    mIdScanStoragePermission && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> mActivityResultLauncher.launch("image/*")

                    // Certification permissions
                    mCertCameraPermission && permissions[Manifest.permission.CAMERA] == true -> takePic(FileType.CERTIFICATION)
                    mCertStoragePermission && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> mActivityResultLauncher.launch("image/*")

                    // Curriculum Vitae (CV) permissions
                    mCvCameraPermission && permissions[Manifest.permission.CAMERA] == true -> takePic(FileType.CV)
                    mCvStoragePermission && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> mActivityResultLauncher.launch("image/*")

                    // Proof of Address permissions
                    mProofOfAddressCameraPermission && permissions[Manifest.permission.CAMERA] == true -> takePic(FileType.PROOF_OF_ADDRESS)
                    mProofOfAddressStoragePermission && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> mActivityResultLauncher.launch("image/*")

                    // Bank Confirmation permissions
                    mBankConfirmationCameraPermission && permissions[Manifest.permission.CAMERA] == true -> takePic(FileType.BANK_CONFIRMATION)
                    mBankConfirmationStoragePermission && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> mActivityResultLauncher.launch("image/*")

                    else -> setAllSelectionToFalse()
                }
            }

        mActivityResultLauncher =
            registerForActivityResult(ResultContracts.SelectFile()) { uri ->
                if (uri != null) {
                    when {
                        mCertStoragePermission -> {
                            mCertFile = File(UriToPath.getPath(requireContext(), uri))
                            mBinding.settingsFinancialCertFileTv.text = mCertFile!!.name
                            mBinding.settingsFinancialCertFileTv.visibility = View.VISIBLE
                        }
                        mCvStoragePermission -> {
                            mCvFile = File(UriToPath.getPath(requireContext(), uri))
                            mBinding.settingsFinancialCVFileTv.text = mCvFile!!.name
                            mBinding.settingsFinancialCVFileTv.visibility = View.VISIBLE
                        }
                        mProofOfAddressStoragePermission -> {
                            mProofOfAddressFile = File(UriToPath.getPath(requireContext(), uri))
                            mBinding.settingsFinancialPOAFileTv.text =
                                mProofOfAddressFile!!.name
                            mBinding.settingsFinancialPOAFileTv.visibility = View.VISIBLE
                        }
                        mBankConfirmationStoragePermission -> {
                            mBankConfirmationFile = File(UriToPath.getPath(requireContext(), uri))
                            mBinding.settingsFinancialBankFileTv.text =
                                mBankConfirmationFile!!.name
                            mBinding.settingsFinancialBankFileTv.visibility =
                                View.VISIBLE
                        }
                        mIdScanStoragePermission -> {
                            mIdScanFile = File(UriToPath.getPath(requireContext(), uri))
                            mBinding.settingsFinancialIdFileTv.text = mIdScanFile!!.name
                            mBinding.settingsFinancialIdFileTv.visibility = View.VISIBLE
                        }
                    }
                }
                setAllSelectionToFalse()
            }

                mTakePicActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && currentFileType != null && ::currentFile.isInitialized) {
                //if (success) {
                    when (currentFileType) {
                        FileType.ID_SCAN -> {
                            mIdScanFile = currentFile
                            mBinding.settingsFinancialIdFileTv.text = mIdScanFile!!.name
                            mBinding.settingsFinancialIdFileTv.visibility = View.VISIBLE
                        }
                        FileType.CERTIFICATION -> {
                            mCertFile = currentFile
                            mBinding.settingsFinancialCertFileTv.text = mCertFile!!.name
                            mBinding.settingsFinancialCertFileTv.visibility = View.VISIBLE
                        }
                        FileType.CV -> {
                            mCvFile = currentFile
                            mBinding.settingsFinancialCVFileTv.text = mCvFile!!.name
                            mBinding.settingsFinancialCVFileTv.visibility = View.VISIBLE
                        }
                        FileType.PROOF_OF_ADDRESS -> {
                            mProofOfAddressFile = currentFile
                            mBinding.settingsFinancialPOAFileTv.text = mProofOfAddressFile!!.name
                            mBinding.settingsFinancialPOAFileTv.visibility = View.VISIBLE
                        }
                        FileType.BANK_CONFIRMATION -> {
                            mBankConfirmationFile = currentFile
                            mBinding.settingsFinancialBankFileTv.text = mBankConfirmationFile!!.name
                            mBinding.settingsFinancialBankFileTv.visibility = View.VISIBLE
                        }

                        else -> {}
                    }
/*  when{
      mIdScanCameraPermission -> {
          mIdScanFile = currentFile
          mBinding.settingsFinancialIdFileTv.text = mIdScanFile!!.name
          mBinding.settingsFinancialIdFileTv.visibility = View.VISIBLE
      }
      mCertCameraPermission ->{
          mCertFile = currentFile
          mBinding.settingsFinancialCertFileTv.text = mCertFile!!.name
          mBinding.settingsFinancialCertFileTv.visibility = View.VISIBLE
      }
      mCvCameraPermission -> {
          mCvFile = currentFile
          mBinding.settingsFinancialCVFileTv.text = mCvFile!!.name
          mBinding.settingsFinancialCVFileTv.visibility = View.VISIBLE
      }
      mProofOfAddressCameraPermission -> {
          mProofOfAddressFile = currentFile
          mBinding.settingsFinancialPOAFileTv.text = mProofOfAddressFile!!.name
          mBinding.settingsFinancialPOAFileTv.visibility = View.VISIBLE
      }
      mBankConfirmationCameraPermission -> {
          mBankConfirmationFile = currentFile
          mBinding.settingsFinancialBankFileTv.text = mBankConfirmationFile!!.name
          mBinding.settingsFinancialBankFileTv.visibility = View.VISIBLE
      }
  }*/
} else {
//currentFile = null // Reset current file if operation was unsuccessful
}
setAllSelectionToFalse()
}
}

override fun onCreateView(
inflater: LayoutInflater,
container: ViewGroup?,
savedInstanceState: Bundle?
): View? {
mBinding = FragSettingsFinancialBinding.inflate(inflater, container, false)
return mBinding.root
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
super.onViewCreated(view, savedInstanceState)
init()
}

private fun init() {
mPresenter = SettingsFinancialPresenterImpl(this)
//mPresenter.getAccountTypes()

mBinding.settingsFinancialCameraIdBtn.setOnClickListener(this)
mBinding.settingsFinancialUploadIdBtn.setOnClickListener(this)
mBinding.settingsFinancialUploadCertBtn.setOnClickListener(this)
mBinding.settingsFinancialUploadCVBtn.setOnClickListener(this)
mBinding.settingsFinancialUploadPOABtn.setOnClickListener(this)
mBinding.settingsFinancialUploadBankBtn.setOnClickListener(this)

initInfo()
}

private fun initInfo() {
    //val loggedInUser = App.loggedInUser!!
    val loggedInUser = App.loggedInUser
    if (loggedInUser == null) {
        Log.e("SettingsFinancialFrag", "loggedInUser is null")
        // Handle null user, such as redirecting to login or showing an error message
        mBinding.settingsFinancialNotApprovedContainer.HeaderInfoTitleTv.text =
            "user not logged in"
        return
    }
    if (App.quickInfo != null && App.quickInfo!!.financialApproval.toBoolean()) {
        val params =
            mBinding.settingsFinancialUploadIdBtn.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = 0
        mBinding.settingsFinancialUploadIdBtn.requestLayout()

        mBinding.settingsFinancialUploadTv.text = getText(R.string.approved)
        mBinding.settingsFinancialUploadCertTv.text = getText(R.string.approved)

        mBinding.settingsFinancialCameraIdBtn.visibility = View.GONE

        mBinding.settingsFinancialUploadCameraImg.visibility = View.GONE
        mBinding.settingsFinancialUploadCameraCertImg.visibility = View.GONE

        // mBinding.settingsFinancialAccountTypeSpinner.isEnabled = false
        mBinding.settingsFinancialBankEdtx.isEnabled = false
        mBinding.settingsFinancialIBANEdtx.isEnabled = false
        mBinding.settingsFinancialAccountIDEdtx.isEnabled = false
        mBinding.settingsFinancialAddressEdtx.isEnabled = false
        mBinding.settingsFinancialUploadIdBtn.isEnabled = false
        mBinding.settingsFinancialCameraIdBtn.isEnabled = false
        mBinding.settingsFinancialUploadCertBtn.isEnabled = false

        mBinding.settingsFinancialIdLabel.visibility = View.GONE;
        mBinding.settingsFinancialIdGrp.visibility = View.GONE;

        mBinding.settingsFinancialPoaLabel.visibility = View.GONE;
        mBinding.settingsFinancialPoaGrp.visibility = View.GONE;

        mBinding.settingsFinancialBankLabel.visibility = View.GONE;
        mBinding.settingsFinancialBankGrp.visibility = View.GONE;

        mBinding.settingsFinancialCertsLabel.visibility = View.GONE;
        mBinding.settingsFinancialCertGrp.visibility = View.GONE;

        mBinding.settingsFinancialCvLabel.visibility = View.GONE;
        mBinding.settingsFinancialCvGrp.visibility = View.GONE;

    } else if (loggedInUser.accountId != null) {
        mBinding.settingsFinancialNotApprovedContainer.HeaderInfoImg.setBackgroundResource(R.drawable.circle_red)
        mBinding.settingsFinancialNotApprovedContainer.HeaderInfoImg.setImageResource(R.drawable.ic_danger)
        mBinding.settingsFinancialNotApprovedContainer.HeaderInfoTitleTv.text =
            getString(R.string.financial_approval)
        mBinding.settingsFinancialNotApprovedContainer.HeaderInfoDescTv.text =
            getString(R.string.financial_approval_not_desc)

        mBinding.settingsFinancialNotApprovedContainer.root.visibility = View.VISIBLE
    }

    if (loggedInUser.accountId != null) {
        mBinding.settingsFinancialAccountIDEdtx.setText(loggedInUser.accountId)
    }

    if (loggedInUser.iban != null) {
        mBinding.settingsFinancialIBANEdtx.setText(loggedInUser.iban)
    }

    if (loggedInUser.accountId != null) {
        mBinding.settingsFinancialAddressEdtx.setText(loggedInUser.address)
    }


    mBinding.settingsFinancialIdLabel.visibility = View.GONE;
    mBinding.settingsFinancialIdGrp.visibility = View.GONE;

    mBinding.settingsFinancialPoaLabel.visibility = View.GONE;
    mBinding.settingsFinancialPoaGrp.visibility = View.GONE;

    mBinding.settingsFinancialBankLabel.visibility = View.GONE;
    mBinding.settingsFinancialBankGrp.visibility = View.GONE;

    mBinding.settingsFinancialCertsLabel.visibility = View.GONE;
    mBinding.settingsFinancialCertGrp.visibility = View.GONE;

    mBinding.settingsFinancialCvLabel.visibility = View.GONE;
    mBinding.settingsFinancialCvGrp.visibility = View.GONE;
}


private fun takePic() {
val dir = File(requireContext().cacheDir, "Documents")
if (!dir.exists())
    dir.mkdirs()

mIdScanFile = File(dir, UUID.randomUUID().toString() + ".jpg")

val uriForFile = FileProvider.getUriForFile(requireContext(),
            requireContext().packageName + ".provider",
                    mIdScanFile!!
                    )

mTakePicActivityResultLauncher.launch(uriForFile)
}

private fun takePic(fileType: FileType) {
    val dir = File(requireContext().cacheDir, "Documents/$fileType")
    if (!dir.exists())
        dir.mkdirs()

    currentFile = File(dir, UUID.randomUUID().toString() + ".jpg")
    currentFileType = fileType

    when(fileType)
    {
        FileType.ID_SCAN -> mIdScanFile = currentFile
        FileType.CERTIFICATION -> mCertFile = currentFile
        FileType.CV -> mCvFile = currentFile
        FileType.PROOF_OF_ADDRESS -> mProofOfAddressFile = currentFile
        FileType.BANK_CONFIRMATION -> mBankConfirmationFile = currentFile
    }
    val uriForFile = FileProvider.getUriForFile(requireContext(),
    requireContext().packageName + ".provider",
            currentFile!!
            )

    mTakePicActivityResultLauncher.launch(uriForFile)
}

override fun onClick(v: View?) {
when (v?.id) {
R.id.settingsFinancialCameraIdBtn -> {
mIdScanCameraPermission = true
takePic(FileType.ID_SCAN)
}

R.id.settingsFinancialUploadIdBtn -> {
mIdScanStoragePermission = true
selectImg()
}
//cert
R.id.settingsFinancialCameraCertBtn ->{
mCertCameraPermission = true
takePic(FileType.CERTIFICATION)
}
R.id.settingsFinancialUploadCertBtn -> {
mCertStoragePermission = true
selectImg()
}
//cv
R.id.settingsFinancialCameraCVBtn ->{
mCvCameraPermission = true
takePic(FileType.CV)

}
R.id.settingsFinancialUploadCVBtn -> {
mCvStoragePermission = true
selectImg()
}
//proofofaddress
R.id.settingsFinancialCameraPOABtn ->{
mProofOfAddressCameraPermission = true
takePic(FileType.PROOF_OF_ADDRESS)
}
R.id.settingsFinancialUploadPOABtn ->
{
  mProofOfAddressStoragePermission = true
  selectImg()
}
//bank confirmation
R.id.settingsFinancialCameraBankBtn->{
mBankConfirmationCameraPermission = true
takePic(FileType.BANK_CONFIRMATION)
}

R.id.settingsFinancialUploadBankBtn ->
{
  mBankConfirmationStoragePermission = true
  selectImg()
}

}
}
/*
private fun requestPermission(permission: String, isCamera: Boolean = false, action: (() -> Unit)? = null) {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
requireContext(),
permission
) != PackageManager.PERMISSION_GRANTED
) {
if (isCamera) mIdScanCameraPermission = true
else action?.invoke()
mPermissionResultLauncher.launch(arrayOf(permission))
} else {
if (isCamera) takePic()
else mActivityResultLauncher.launch("image/*")
}
}
*/

*/
private fun selectImg() {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
requireContext(),
Manifest.permission.READ_EXTERNAL_STORAGE
) != PackageManager.PERMISSION_GRANTED
) {
mPermissionResultLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
} else {
mActivityResultLauncher.launch("image/*")
}
}

override fun onSaveClicked() {
//val accountType = mBinding.settingsFinancialAccountTypeSpinner.selectedItem
val iban = mBinding.settingsFinancialIBANEdtx.text.toString()
val accountId = mBinding.settingsFinancialAccountIDEdtx.text.toString()
val address = mBinding.settingsFinancialAddressEdtx.text.toString()

//  if (accountType == null) {
//    mBinding.settingsFinancialAccountTypeSpinner.requestFocus()
//     return
//}

if (iban.isEmpty()) {
mBinding.settingsFinancialIBANEdtx.error = ""
mBinding.settingsFinancialIBANEdtx.requestFocus()
return
}

if (accountId.isEmpty()) {
mBinding.settingsFinancialAccountIDEdtx.error = ""
mBinding.settingsFinancialAccountIDEdtx.requestFocus()
return
}

if (address.isEmpty()) {
mBinding.settingsFinancialAddressEdtx.error = ""
mBinding.settingsFinancialAddressEdtx.requestFocus()
return
}

val loggedInUser = App.loggedInUser!!

if(loggedInUser.isInstructor()){

        if (mIdScanFile == null || mCertFile == null) {
            val message = if (mIdScanFile == null) {
            getString(R.string.choose_your_id_scan_file)
        } else {
        getString(R.string.choose_your_id_certificate_file)
        }

        ToastMaker.show(
        requireContext(),
        getString(R.string.error),
        message,
        ToastMaker.Type.ERROR
        )

        return
    }
// Add validation for new file sections here
if (mCvFile == null || mProofOfAddressFile == null || mBankConfirmationFile == null) {
val missingFileMessage = when {
mCvFile == null -> getString(R.string.choose_your_cv_file)
mProofOfAddressFile == null -> getString(R.string.choose_your_proof_of_address_file)
mBankConfirmationFile == null -> getString(R.string.choose_your_bank_confirmation_file)
else -> null
}
missingFileMessage?.let {
ToastMaker.show(
  requireContext(),
  getString(R.string.error),
  it,
  ToastMaker.Type.ERROR
)
}
return
}

}
mLoadingDialog = LoadingDialog.instance
mLoadingDialog.show(childFragmentManager, null)

val financialSettings = FinancialSettings()
financialSettings.accountId = accountId
//  financialSettings.accountType = accountType as String
financialSettings.address = address
financialSettings.iban = iban

mPresenter.uploadFinancialSettings(financialSettings)
//mPresenter.uploadFinancialSettingsFiles(mIdScanFile!!, mCertFile!!)
val fileParts = listOfNotNull(
mIdScanFile?.let { file ->
MultipartBody.Part.createFormData("identity_scan", file.name, file.asRequestBody())
},
mCertFile?.let { file ->
MultipartBody.Part.createFormData("certificate", file.name, file.asRequestBody())
},
mCvFile?.let { file ->
MultipartBody.Part.createFormData("cv", file.name, file.asRequestBody())
},
mProofOfAddressFile?.let { file ->
MultipartBody.Part.createFormData("proof_of_address", file.name, file.asRequestBody())
},
mBankConfirmationFile?.let { file ->
MultipartBody.Part.createFormData("bank_confirmation", file.name, file.asRequestBody())
}
)

mPresenter.uploadFinancialSettingsFiles(*fileParts.toTypedArray())

}

override fun initTab() {
(parentFragment as SettingsFrag).changeSaveBtnEnable(true)

if (App.quickInfo != null && App.quickInfo!!.financialApproval.toBoolean()) {
(parentFragment as SettingsFrag).changeSaveBtnVisibility(false)
} else {
(parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
}
}

fun onAccountTypesReceived(types: Count<String>) {
val adapter = ItemSpinnerAdapter(requireContext(), types.items)
//    mBinding.settingsFinancialAccountTypeSpinner.adapter = adapter

val loggedInUser = App.loggedInUser!!
if (loggedInUser.accountType != null) {
for ((index, item) in types.items.withIndex()) {
if (item == loggedInUser.accountType) {
 // mBinding.settingsFinancialAccountTypeSpinner.setSelection(index)
  break
}
}
}
//        mBinding.settingsFinancialAccountTypeSpinner.onItemSelectedListener = this
}

fun onFilesSaved(response: BaseResponse) {
if (response.isSuccessful) {
mFilesSaved = true
checkIfBothAreSaved(response)
} else {
mLoadingDialog.dismiss()
ToastMaker.show(
requireContext(),
getString(R.string.error),
response.message,
ToastMaker.Type.ERROR
)
}
}

private fun checkIfBothAreSaved(response: BaseResponse) {
if (mFilesSaved && mSettingsSaved) {

mLoadingDialog.dismiss()

ToastMaker.show(
requireContext(),
getString(R.string.success),
response.message,
ToastMaker.Type.SUCCESS
)

mFilesSaved = false
mSettingsSaved = false
}
}

fun onSettingsSaved(
response: BaseResponse,
financialSettings: FinancialSettings
) {
if (response.isSuccessful) {
val loggedInUser = App.loggedInUser!!
loggedInUser.accountId = financialSettings.accountId
loggedInUser.accountType = financialSettings.accountType
loggedInUser.address = financialSettings.address
loggedInUser.iban = financialSettings.iban

mSettingsSaved = true
checkIfBothAreSaved(response)
} else {
mLoadingDialog.dismiss()
ToastMaker.show(
requireContext(),
getString(R.string.error),
response.message,
ToastMaker.Type.ERROR
)
}
}

// Generic method to handle button clicks for taking pictures
/* private fun handleCameraButtonClick(fileVariable: File?) {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
requireContext(),
Manifest.permission.CAMERA
) != PackageManager.PERMISSION_GRANTED
) {
when (fileVariable) {
mCertFile -> mCertStoragePermission = true
mIdScanFile -> mIdScanCameraPermission = true
mCvFile -> mCvStoragePermission = true
mProofOfAddressFile -> mProofOfAddressStoragePermission = true
mBankConfirmationFile -> mBankConfirmationStoragePermission = true
}
// Adjust this flag as needed
mPermissionResultLauncher.launch(arrayOf(Manifest.permission.CAMERA))
} else {
takePic(fileVariable) // Pass the appropriate file variable
}
}

// Generic method to take a picture and save to a specified file
private fun takePic(fileVariable: File?) {
val dir = File(requireContext().cacheDir, "Documents")
if (!dir.exists())
dir.mkdirs()

val file = File(dir, UUID.randomUUID().toString() + ".jpg")

// Dynamically assign the file variable
when (fileVariable) {
mCertFile -> mCertFile = file
mIdScanFile -> mIdScanFile = file
mCvFile -> mCvFile = file
mProofOfAddressFile -> mProofOfAddressFile = file
mBankConfirmationFile -> mBankConfirmationFile = file
}

val uriForFile = FileProvider.getUriForFile(
requireContext(),
requireContext().packageName + ".provider",
file
)

mTakePicActivityResultLauncher.launch(uriForFile)
}*/

}