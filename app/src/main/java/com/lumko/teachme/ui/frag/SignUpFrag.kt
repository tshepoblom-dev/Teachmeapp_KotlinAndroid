package com.lumko.teachme.ui.frag

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragSignUpBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.BuildVars
import com.lumko.teachme.manager.ResponseStatus
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.SignUpPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.UserAuthFrag
import com.lumko.teachme.ui.widget.LoadingDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

class SignUpFrag : UserAuthFrag(), SelectionDialog.ItemSelection<Country>{

    private lateinit var qualificationFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var cvFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var idDocumentFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var proofOfAddressFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var bankAccountLetterFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var mPresenter: Presenter.SignUpPresenter
    private lateinit var mBinding: FragSignUpBinding
    private lateinit var mAccountTypeRadioGroup: RadioGroup
    private var mCountry: Country? = null
    private var mIsEmail = false
    private var qualificationBytes: ByteArray? = null
    private var cvBytes: ByteArray? = null
    private var idBytes: ByteArray? = null
    private var poaBytes: ByteArray? = null
    private var bankAccBytes: ByteArray? = null
    private var fileList:MutableList<MultipartBody.Part> = mutableListOf<MultipartBody.Part>()

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableLoginBtn()
        }
    }

    companion object {
        private const val TAG = "SignInFrag"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSignUpBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        mGoogleBtn = mBinding.signUpGoogleLayout
        mFacebookBtn = mBinding.signUpFacebookLayout

        mBinding.signUpCountryImg.setOnClickListener(this)
        mBinding.signUpCreateAccountBtn.setOnClickListener(this)
        mBinding.signUpSignInBtn.setOnClickListener(this)
        mBinding.signUpEmailPhoneEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signUpPasswordEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signUpRetypePasswordEdtx.addTextChangedListener(mInputTextWatcher)

        // Set references for new RadioGroup for accountType
        mAccountTypeRadioGroup = mBinding.radioGroupAccountType

       // mBinding.accountTypeEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.idNumberEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.institutionNameEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.courseEdtx.addTextChangedListener(mInputTextWatcher)

        // File field views
        val qualificationField = mBinding.uploadQualificationBtn
        val cvField = mBinding.uploadCvBtn
        val idDocumentField = mBinding.uploadIdDocumentBtn
        val proofOfAddressField = mBinding.uploadProofOfAddressBtn
        val bankAccountLetterField = mBinding.uploadBankLetterBtn

        // Set onClickListeners for file upload buttons
        qualificationField.setOnClickListener(this)
        cvField.setOnClickListener(this)
        idDocumentField.setOnClickListener(this)
        proofOfAddressField.setOnClickListener(this)
        bankAccountLetterField.setOnClickListener(this)

        //hide file controls at first
        qualificationField.visibility = View.GONE
        cvField.visibility = View.GONE
        idDocumentField.visibility = View.GONE
        proofOfAddressField.visibility = View.GONE
        bankAccountLetterField.visibility = View.GONE

        mBinding.qualificationTv.visibility = View.GONE
        mBinding.cvTv.visibility = View.GONE
        mBinding.proofOfAddressTv.visibility = View.GONE
        mBinding.idDocumentTv.visibility = View.GONE
        mBinding.bankAccountLetterTv.visibility = View.GONE

        mGoogleBtn.visibility = View.GONE
        mFacebookBtn.visibility = View.GONE
        // Set listener for RadioGroup
        mAccountTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAccountType1 -> {
                    // Hide file fields when accountType 1 is selected
                    qualificationField.visibility = View.GONE
                    cvField.visibility = View.GONE
                    idDocumentField.visibility = View.GONE
                    proofOfAddressField.visibility = View.GONE
                    bankAccountLetterField.visibility = View.GONE

                    mBinding.qualificationTv.visibility = View.GONE
                    mBinding.cvTv.visibility = View.GONE
                    mBinding.proofOfAddressTv.visibility = View.VISIBLE
                    mBinding.idDocumentTv.visibility = View.VISIBLE
                    mBinding.bankAccountLetterTv.visibility = View.GONE
                }

                R.id.radioAccountType2 -> {
                    // Show file fields when accountType 2 is selected
                    qualificationField.visibility = View.VISIBLE
                    cvField.visibility = View.VISIBLE
                    idDocumentField.visibility = View.VISIBLE
                    proofOfAddressField.visibility = View.VISIBLE
                    bankAccountLetterField.visibility = View.VISIBLE

                    mBinding.qualificationTv.visibility = View.VISIBLE
                    mBinding.cvTv.visibility = View.VISIBLE
                    mBinding.proofOfAddressTv.visibility = View.VISIBLE
                    mBinding.idDocumentTv.visibility = View.VISIBLE
                    mBinding.bankAccountLetterTv.visibility = View.VISIBLE
                }
            }
        }
        mPresenter = SignUpPresenterImpl(this)

        if (App.appConfig.registrationMethod == App.Companion.Registration.EMAIL.value()) {
            mIsEmail = true
            mBinding.signUpCountryImg.visibility = View.GONE
            mBinding.signUpCountryImg.visibility = View.GONE
            mBinding.signUpEmailPhoneEdtx.hint = getString(R.string.email)
            mBinding.signUpEmailPhoneEdtx.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        // Initialize the ActivityResultLaunchers
        qualificationFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                val selectedFileUri = result.data!!.data

                // Extract the file name from the Uri
                if(selectedFileUri != null) {
                    val fileName = getFileNameFromUri(selectedFileUri)
                    handleFileUpload(selectedFileUri, "qualification")
                    // Set the filename to the TextView (assuming you're in a Fragment and using ViewBinding)
                    mBinding.qualificationTv.text = fileName
                }
            }
        }

        cvFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                val selectedFileUri = result.data!!.data
                handleFileUpload(selectedFileUri, "cv")
                // Extract the file name from the Uri
                if(selectedFileUri != null) {
                    val fileName = getFileNameFromUri(selectedFileUri)

                    // Set the filename to the TextView (assuming you're in a Fragment and using ViewBinding)
                    mBinding.cvTv.text = fileName
                }
            }
        }

        idDocumentFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                val selectedFileUri = result.data!!.data
                handleFileUpload(selectedFileUri, "idDocument")
                // Extract the file name from the Uri
                if(selectedFileUri != null) {
                    val fileName = getFileNameFromUri(selectedFileUri)

                    // Set the filename to the TextView (assuming you're in a Fragment and using ViewBinding)
                    mBinding.idDocumentTv.text = fileName
                }
            }
        }

        proofOfAddressFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                val selectedFileUri = result.data!!.data
                handleFileUpload(selectedFileUri, "proofOfAddress")
                // Extract the file name from the Uri
                if(selectedFileUri != null) {
                    val fileName = getFileNameFromUri(selectedFileUri)

                    // Set the filename to the TextView (assuming you're in a Fragment and using ViewBinding)
                    mBinding.proofOfAddressTv.text = fileName
                }
            }
        }

        bankAccountLetterFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result ->
            if(result.resultCode == Activity.RESULT_OK && result.data?.data != null){
                val selectedFileUri = result.data!!.data
                handleFileUpload(selectedFileUri, "bankAccountLetter")

                // Extract the file name from the Uri
                if(selectedFileUri != null) {
                    val fileName = getFileNameFromUri(selectedFileUri)

                    // Set the filename to the TextView (assuming you're in a Fragment and using ViewBinding)
                    mBinding.bankAccountLetterTv.text = fileName
                }
            }

        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {
            R.id.signUpCreateAccountBtn -> {
                if (!arePasswordFieldsEqual())
                    return

                val password = mBinding.signUpPasswordEdtx.text.toString()
                val passwordConfirmation = mBinding.signUpRetypePasswordEdtx.text.toString()
                // Get selected account type from RadioGroup
                val selectedAccountTypeId = mAccountTypeRadioGroup.checkedRadioButtonId

                val accountType = when (selectedAccountTypeId) {
                    R.id.radioAccountType1 -> 1
                    R.id.radioAccountType2 -> 2
                    else -> 1 // Default or error case
                }

                if (mIsEmail) {
                    val email = mBinding.signUpEmailPhoneEdtx.text.toString()
                    val signUp = EmailSignUp()
                    signUp.password = password
                    signUp.passwordConfirmation = passwordConfirmation
                    signUp.email = email
                    signUp.accountType = accountType
                    signUp.idNumber = mBinding.idNumberEdtx.text.toString() // New field
                    signUp.institutionName = mBinding.institutionNameEdtx.text.toString() // New field
                    signUp.course = mBinding.courseEdtx.text.toString() // New field

                    if(accountType == 1){
                        mPresenter.signUp(signUp)
                    }else if(accountType ==2){
                        mPresenter.signUp(signUp, fileList)
                    }
                } else {
                    val mobile = mBinding.signUpEmailPhoneEdtx.text.toString()
                    val signUp = MobileSignUp()
                    signUp.password = password
                    signUp.passwordConfirmation = passwordConfirmation
                    signUp.countryCode = mCountry?.callingCode!!
                    signUp.mobile = mobile
                    signUp.accountType = accountType
                    signUp.idNumber = mBinding.idNumberEdtx.text.toString() // New field
                    signUp.institutionName = mBinding.institutionNameEdtx.text.toString() // New field
                    signUp.course = mBinding.courseEdtx.text.toString() // New field
                    if(accountType == 1){
                        mPresenter.signUp(signUp)
                    }else if(accountType ==2){
                        mPresenter.signUp(signUp, fileList)
                    }
                }
            }

            R.id.signUpCountryImg -> {
                val bundle = Bundle()
                bundle.putSerializable(App.SELECTION_TYPE, SelectionDialog.Selection.Country)

                val instance = SelectionDialog.getInstance<Country>()
                instance.setOnItemSelected(this)
                instance.arguments = bundle
                instance.show(childFragmentManager, null)
            }


            R.id.signUpSignInBtn -> {
                val frag = SignInFrag()
                parentFragmentManager.beginTransaction().replace(android.R.id.content, frag)
                    .commit()
            }
            // Handle file upload button clicks
            R.id.uploadQualificationBtn -> {
                selectFile(qualificationFileLauncher)
            }
            R.id.uploadCvBtn -> {
                selectFile(cvFileLauncher)
            }
            R.id.uploadIdDocumentBtn -> {
                selectFile(idDocumentFileLauncher)
            }
            R.id.uploadProofOfAddressBtn -> {
                selectFile(proofOfAddressFileLauncher)
            }
            R.id.uploadBankLetterBtn ->{
                selectFile(bankAccountLetterFileLauncher)
            }
        }
    }
     private fun selectFile(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // You can specify a specific MIME type like "application/pdf"
        launcher.launch(Intent.createChooser(intent, "Select File"))
    }
    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "unknown"

        // Check if the Uri is a content Uri
        if (uri.scheme.equals("content")) {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    // Get the display name from the cursor
                    fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        } else if (uri.scheme.equals("file")) {
            // If it's a file Uri, extract the filename from the path
            fileName = uri.lastPathSegment ?: "unknown"
        }

        return fileName
    }

    // Function to handle the actual file upload to server
    private fun handleFileUpload(fileUri: Uri?, fileType: String) {
        // Implement your file upload logic here, possibly sending the fileUri to your backend server
        //fileList = mutableListOf<MultipartBody.Part>()
        if (fileUri != null) {
            // TODO: Upload file to the server
            try {
                // Use ContentResolver to open the InputStream from the URI
                val inputStream = context?.contentResolver?.openInputStream(fileUri)

                // Save the file content into a temporary file in the cache directory
                val file = File(context?.cacheDir, "${System.currentTimeMillis()}_${fileType}")
                val outputStream = FileOutputStream(file)

                // Copy the InputStream data to the file
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Create a RequestBody from the file for Retrofit
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData(fileType, file.name, requestBody)

                // Add the file part to the list for uploading
                fileList.add(part)
            }
            catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            catch (e: Exception)
            {
                Log.e("FileUpload", "Error reading file for $fileType: ${e.message}")
            }
        } else {
            Log.e("FileUpload", "File URI is null for $fileType")
        }
    }
    private fun matchToVar(byteArray: ByteArray, fileType: String){
        when(fileType){
            "qualification" -> {
                qualificationBytes = byteArray
            }
            "cv" -> {
                cvBytes = byteArray
            }
            "idDocument" -> {
                idBytes = byteArray
            }
            "proofOfAddress" -> {
                poaBytes = byteArray
            }
            "bankAccountLetter" -> {
                bankAccBytes = byteArray
            }
        }
    }

    fun compressData(data: ByteArray): ByteArray {
        val byteStream = ByteArrayOutputStream()
        try {
            GZIPOutputStream(byteStream).use { gzip ->
                gzip.write(data)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return byteStream.toByteArray()
    }
//    fun onCodeSent(response: Response, email: String?, mobile: String?) {
//        if (response.isSuccessful) {
//            val frag = VerifyCodeFrag.getInstance()
//            val bundle = Bundle()
//            bundle.putString(App.TOKEN, response.token)
//            bundle.putInt(App.CODE_VAL_TIME, response.codeValidationTime)
//            if (email != null)
//                bundle.putString(App.EMAIL, email)
//
//            if (mobile != null)
//                bundle.putString(App.MOBILE, mobile)
//
//            frag.arguments = bundle
//            parentFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
//
//        } else {
//            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {

            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.

            val login = ThirdPartyLogin()
            login.email = account.email!!
            login.id = account.id!!
            login.name = account.displayName!!

            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog?.show(childFragmentManager, null)
            mPresenter.googleSignInUp(login)

        } catch (e: ApiException) {
            mLoadingDialog?.dismiss()

            val rsp = BaseResponse()
            rsp.message = getString(R.string.sign_in_failed)
            onErrorOccured(rsp)

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            if (BuildVars.LOGS_ENABLED){
                val res = BaseResponse()
                res.isSuccessful = false
                res.message = "signInResult:failed code=" + e.statusCode + "\n" + e.message

                ToastMaker.show(requireContext(), res)
                Log.d(TAG, "signInResult:failed code=" + e.statusCode)
            }
        }
    }

    override fun getFacebookSignInCallback(): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result != null) {
                    val request = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { _, response ->
                        if (response != null) {
                            try {
                                val email = response.jsonObject.getString("email")
                                val name = response.jsonObject.getString("name")

                                if (email.isNullOrEmpty()) {
                                    val rsp = BaseResponse()
                                    rsp.message =
                                        getString(R.string.no_email_associated_with_this_account)
                                    onErrorOccured(rsp)
                                    return@newMeRequest
                                }

                                val login = ThirdPartyLogin()
                                login.email = email
                                login.id = result.accessToken.userId
                                login.name = name

                                mLoadingDialog = LoadingDialog.instance
                                mLoadingDialog?.show(childFragmentManager, null)
                                mPresenter.facebookSignInUp(login)

                            } catch (ex: JSONException) {
                                val rsp = BaseResponse()
                                rsp.message = getString(R.string.sign_in_failed)
                                onErrorOccured(rsp)
                            }
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAsync()
                }
            }

            override fun onCancel() {
                val rsp = BaseResponse()
                rsp.message = getString(R.string.sign_in_canceled)
                onErrorOccured(rsp)
            }

            override fun onError(error: FacebookException?) {
                val rsp = BaseResponse()
                rsp.message = getString(R.string.sign_in_failed)
                onErrorOccured(rsp)
            }
        }
    }

//    fun onCustomerSaved(response: retrofit2.Response<Customer>) {
//        val customer = response.body()
//        if (customer != null) {
//    if (customer.via == App.RegistrationProvider.GOOGLE.value())
//    {
//        mGoogleSignInClient.signOut()
//    } else if (customer.via == App.RegistrationProvider.FACEBOOK.value())
//    {
//        LoginManager.getInstance().logOut()
//    }
//
//            App.setCustomer(customer)
//            ApiService.createAuthorizedApiService(requireContext(), customer.token)
//            context?.let { AppKotlin.saveTokenAndEmaileOrMobile(it, customer) }
//
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            intent.putExtra(App.IS_LOGIN, true)
//            intent.putExtra(App.SHOULD_REGISTER, customer.shouldRegister())
//            intent.putExtra(App.TOKEN, customer.token)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//            activity?.finish()
//        } else {
//            mLoadingDialog.dismiss()
//            val rsp = Gson().fromJson<BaseResponse>(
//                response.errorBody()?.string(),
//                BaseResponse::class.java
//            )
//            Toast.makeText(requireContext(), rsp.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun enableDisableLoginBtn() {
        val username = mBinding.signUpEmailPhoneEdtx.text.toString()
        val password = mBinding.signUpPasswordEdtx.text.toString()
        val passwordRetype = mBinding.signUpRetypePasswordEdtx.text.toString()
        val loginBtn = mBinding.signUpCreateAccountBtn
        loginBtn.isEnabled = false

        if (username.isNotEmpty() && password.isNotEmpty() && passwordRetype.isNotEmpty()) {

            if (mIsEmail) {
                val isValidEmail = username.length >= 3 && username.contains("@")
                        && username.contains(".")
                if (isValidEmail) {
                    loginBtn.isEnabled = true
                }

            } else {
                var enable = true

                if (mCountry != null) {
                    for ((index, char) in username.withIndex()) {
                        if (index == 0 && char == '+') {
                            continue
                        }

                        if (!char.isDigit()) {
                            enable = false
                            break
                        }
                    }
                } else {
                    enable = false
                }

                loginBtn.isEnabled = enable
            }

        } else if (loginBtn.isEnabled) {
            loginBtn.isEnabled = false
        }
    }


    private fun arePasswordFieldsEqual(): Boolean {
        val password = mBinding.signUpPasswordEdtx.text.toString()
        val passwordRetype = mBinding.signUpRetypePasswordEdtx.text.toString()
        if (password.isNotEmpty() && passwordRetype.isNotEmpty() && password != passwordRetype) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.password),
                getString(R.string.make_sure_passwords_are_the_same),
                ToastMaker.Type.ERROR
            )

            return false
        }

        return true
    }


    override fun onItemSelected(country: Country) {
        mCountry = country
        country.img?.let { mBinding.signUpCountryImg.setImageResource(it) }
        enableDisableLoginBtn()
    }

    fun onUserBasicsSaved(
        data: Data<User>,
        emailSignUp: EmailSignUp? = null,
        mobileSignUp: MobileSignUp? = null
    ) {
        if (data.isSuccessful || data.status == ResponseStatus.AUTH_GO_TO_STEP2.value()) {
            val frag = VerifyAccountFrag()
            val bundle = Bundle()
            bundle.putInt(App.USER_ID, data.data!!.userId)
            if (emailSignUp != null) {
                bundle.putParcelable(App.SIGN_UP, emailSignUp)
            } else {
                bundle.putParcelable(App.SIGN_UP, mobileSignUp)
            }
            frag.arguments = bundle
            parentFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
        } else if (data.status == ResponseStatus.AUTH_GO_TO_STEP3.value()) {

            activity?.finish()

            if (mSignInRequest) {
                val intent = Intent()
                intent.putExtra(App.SHOULD_REGISTER, true)
                intent.putExtra(App.USER_ID, data.data!!.userId)
                activity?.setResult(Activity.RESULT_OK, intent)
            } else {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(App.SHOULD_REGISTER, true)
                intent.putExtra(App.USER_ID, data.data!!.userId)
                startActivity(intent)
            }
        } else {
            onErrorOccured(data)
        }
    }
}