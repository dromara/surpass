import i18n from '../languages/index'
import type {DistData} from "@/types/Dict"

const {t} = i18n.global;

const distData: DistData = {
    "sys_normal_disable": [
        {
            label: "正常",
            value: 0
        },
        {
            label: "禁用",
            value: 1
        }
    ],
    "sys_normal_disable2": [
        {
            label: "正常",
            value: "0"
        },
        {
            label: "禁用",
            value: "1"
        }
    ],
    "sys_disable": [
        {
            label: "是",
            value: "y"
        },
        {
            label: "否",
            value: "n"
        }
    ],
    "sys_show_hide": [
        {
            label: "显示",
            value: 0
        },
        {
            label: "隐藏",
            value: 1
        }
    ],

    "sys_frame": [
        {
            label: "是",
            value: 1
        },
        {
            label: "否",
            value: 0
        }
    ],
    "sys_cache": [
        {
            label: "否",
            value: 0
        },
        {
            label: "是",
            value: 1
        }
    ],

    "sys_visible_hide": [
        {
            label: "显示",
            value: 1
        },
        {
            label: "隐藏",
            value: 0
        }
    ],
    "sys_user_sex": [
        {
            label: "其他",
            value: 0
        },
        {
            label: "男",
            value: 1
        },
        {
            label: "女",
            value: 2
        }
    ],

    "sys_classify": [
        {
            label: "目录",
            value: "directory"
        },
        {
            label: "菜单",
            value: "menu"
        },
        {
            label: "按钮",
            value: "button"
        },
        {
            label: "页面",
            value: "page"
        },
        {
            label: "API",
            value: "api"
        },
        {
            label: "其他",
            value: "other"
        }
    ],
    "sys_object_from": [
        {
            label: "LDAP",
            value: 1
        },
        {
            label: "本地",
            value: 0
        }
    ],
    "org_classify": [
        {
            label: "部门",
            value: "department"
        },
        {
            label: "组织",
            value: "organization"
        }
    ],

    "sys_cron_list": [
        {
            label: "2小时执行一次",
            value: "0 0 0/2 * * ?"
        },
        {
            label: "4小时执行一次",
            value: "0 0 0/4 * * ?"
        },
        {
            label: "6小时执行一次",
            value: "0 0 0/6 * * ?"
        },
        {
            label: "8小时执行一次",
            value: "0 0 0/8 * * ?"
        },
        {
            label: "12小时执行一次",
            value: "0 0 0/12 * * ?"
        }
    ],
    "sys_sync_type": [
        {
            label: "组织",
            value: "0"
        },
        {
            label: "用户",
            value: "1"
        },
        {
            label: "组",
            value: "2"
        }
    ],
    "sys_enable_status": [
        {
            label: "开启",
            value: 0
        },
        {
            label: "关闭",
            value: 1
        }
    ],
    "sys_sync_result_status": [
        {
            label: "成功",
            value: 0
        },
        {
            label: "失败",
            value: 1
        }
    ],
    "sys_sync_action_type": [
        {
            label: "新增",
            value: "add"
        },
        {
            label: "修改",
            value: "update"
        },
        {
            label: "删除",
            value: "delete"
        }
    ],
    "sys_data_object_from": [
        {
            label: "openLdap",
            value: "openldap"
        },
        {
            label: "本地",
            value: "system"
        },
        {
            label: "钉钉",
            value: "dingding"
        },
        {
            label: "飞书",
            value: "feishu"
        },
        {
            label: "企业微信",
            value: "workweixin"
        }
    ],
    "sys_socials_type": [
        {
            label: "微信",
            value: "wechatopen"
        },
        {
            label: "企业微信",
            value: "workweixin"
        },
        {
            label: "微博",
            value: "sinaweibo"
        },
        {
            label: "钉钉",
            value: "dingtalk"
        },
        {
            label: "飞书",
            value: "feishu"
        },
        {
            label: "支付宝",
            value: "alipay"
        },
        {
            label: "抖音",
            value: "douyin"
        }
    ],
    "scanCode_type": [
        {
            label: "无",
            value: "NONE"
        },
        {
            label: "本地",
            value: "LOCAL"
        },
        {
            label: "第三方",
            value: "SOCIAL"
        }
    ],

    "org_type": [
        {
            label: t('jbx.organizations.typeGroup'),
            value: "group"
        },
        {
            label: t('jbx.organizations.typeCompany'),
            value: "company"
        },
        {
            label: t('jbx.organizations.typeDivision'),
            value: "division"
        },
        {
            label: t('jbx.organizations.typeDepartment'),
            value: "department"
        },
        {
            label: t('jbx.organizations.typeTeam'),
            value: "team"
        },
        {
            label: t('jbx.organizations.typeEntity'),
            value: "entity"
        },
        {
            label: t('jbx.organizations.typeVirtual'),
            value: "virtual"
        }
    ],
    "use_gender": [
        {

            label: t('jbx.users.genderFemale'),
            value: '1'
        },
        {

            label: t('jbx.users.genderMale'),
            value: '2'
        }
    ],
    "group_type": [
        {

            label: t('jbx.roles.category.dynamic'),
            value: 'dynamic'
        },
        {

            label: t('jbx.roles.category.static'),
            value: 'static'
        },
        {

            label: t('jbx.roles.category.app'),
            value: 'app'
        }
    ],
    "group_category": [
        {
            label: t('jbx.roles.category.dynamic'),
            value: 'dynamic'
        },
        {

            label: t('jbx.roles.category.static'),
            value: 'static'
        },
        {

            label: t('jbx.roles.category.app'),
            value: 'app'
        }
    ],
    //操作类型
    "action_type": [
        {
            label: t('jbx.resources.actionType.read'),
            value: 'r',
        },
        {
            label: t('jbx.resources.actionType.list'),
            value: 'l',
        },
        {
            label: t('jbx.resources.actionType.write'),
            value: 'w',
        }
    ],
    "captcha_type": [
        {
            label: t('jbx.institutions.captchaNone'),
            value: "NONE"
        },
        {
            label: t('jbx.institutions.captchaText'),
            value: "TEXT"
        },
        {
            label: t('jbx.institutions.captchaArithmetic'),
            value: "ARITHMETIC"
        }
    ],
    "create_type": [
        {
            label: t('jbx.text.automatic'),
            value: "automatic"
        },
        {
            label: t('jbx.text.manual'),
            value: "manual"
        }
    ],
    "account_mapping": [
        {
            label: "username",
            value: "username"
        },
        {
            label: "employeeNumber",
            value: "employeeNumber"
        },
        {
            label: "windowsAccount",
            value: "windowsAccount"
        },
        {
            label: "email",
            value: "email"
        },
        {
            label: "mobile",
            value: "mobile"
        },
        {
            label: "idCardNo",
            value: "idCardNo"
        }
    ],
    "sync_source_type": [
        {
            label: "SCIM v2.0",
            value: "SCIM v2.0"
        },
        {
            label: "API",
            value: "API"
        },
        {
            label: "MSAD",
            value: "MSAD"
        },
        {
            label: "LDAP",
            value: "LDAP"
        },
        {
            label: "DATABASE",
            value: "DATABASE"
        },
    ],
    //组织类型
    "organizations_type": [
        {
            label: t('jbx.organizations.typeGroup'),
            value: 'group'
        },
        {
            label: t('jbx.organizations.typeCompany'),
            value: 'company'
        },
        {
            label: t('jbx.organizations.typeDivision'),
            value: 'division'
        },
        {
            label: t('jbx.organizations.typeDepartment'),
            value: 'department'
        },
        {
            label: t('jbx.organizations.typeTeam'),
            value: 'team'
        },
        {
            label: t('jbx.organizations.typeEntity'),
            value: 'entity'
        },
        {
            label: t('jbx.organizations.typeVirtual'),
            value: 'virtual'
        }
    ],
    "protocol_type": [
        {
            label: 'OAuth_v2.0',
            value: 'OAuth_v2.0'
        },
        {
            label: 'OpenID_Connect_v1.0',
            value: 'OpenID_Connect_v1.0'
        },
        {
            label: 'JWT',
            value: 'JWT'
        },
        {
            label: 'Token_Based',
            value: 'Token_Based'
        },
        {
            label: 'CAS',
            value: 'CAS'
        },
        {
            label: 'SAML_v2.0',
            value: 'SAML_v2.0'
        },
        {
            label: 'Extend_API',
            value: 'Extend_API'
        },
        {
            label: 'Form_Based',
            value: 'Form_Based'
        },
        {
            label: 'Basic',
            value: 'Basic'
        }
    ],
    "data_type": [
        {
            label: t('jbx.expandattrs.typeVarchar'),
            value: 'varchar'
        },
        {
            label: t('jbx.expandattrs.typeInt'),
            value: 'int'
        },
        {
            label: t('jbx.expandattrs.typeBoolean'),
            value: 'boolean'
        }
    ],
    "extend_obj": [
        {
            label: t('jbx.expandattrs.user'),
            value: 'user'
        },
        {
            label: t('jbx.expandattrs.org'),
            value: 'org'
        }
    ],
    "authorizedGrantTypes": [
        {
            label: 'authorization_code',
            value: 'authorization_code'
        },
        {
            label: 'password',
            value: 'password'
        },
        {
            label: 'client_credentials',
            value: 'client_credentials'
        },
        {
            label: 'implicit',
            value: 'implicit'
        },
        {
            label: 'id_token',
            value: 'id_token'
        },
        {
            label: 'token',
            value: 'token'
        },
        {
            label: 'refresh_token',
            value: 'refresh_token'
        }
    ],
    "scope_types": [
        {
            label: 'read',
            value: 'read'
        },
        {
            label: 'write',
            value: 'write'
        },
        {
            label: 'trust',
            value: 'trust'
        },
        {
            label: 'openid',
            value: 'openid'
        },
        {
            label: 'profile',
            value: 'profile'
        },
        {
            label: 'email',
            value: 'email'
        },
        {
            label: 'phone',
            value: 'phone'
        },
        {
            label: 'address',
            value: 'address'
        }
    ],
    "logoutType": [
        {
            label: t('jbx.apps.logoutTypeNone'),
            value: '0'
        },
        {
            label: t('jbx.apps.logoutTypeBack_channel'),
            value: '1'
        },
        {
            label: t('jbx.apps.logoutTypeFront_channel'),
            value: '2'
        }
    ],
    "purview_types": [
        {
            label: t('jbx.apps.visibleHidden'),
            value: '0'
        },
        {
            label: t('jbx.apps.visibleAll'),
            value: '1'
        },
        {
            label: t('jbx.apps.visibleInternet'),
            value: '2'
        },
        {
            label: t('jbx.users.userTypeExternal'),
            value: '3'
        }
    ],
    "inducer_types": [
        {
            label: 'IDP',
            value: 'IDP'
        },
        {
            label: 'SP',
            value: 'SP'
        }
    ],
    "journalAccout_category_types": [
        {
            label: '银行',
            value: 'deposit'
        },
        {
            label: '现金',
            value: 'cash'
        }
    ],
    "sys_yes_no": [
        {
            label: t('jbx.text.no'),
            value: '1'
        },
        {
            label: t('jbx.text.yes'),
            value: '0'
        }
    ],
    "sys_start_stop": [
        {
            label: t('jbx.apps.isAdapterYes'),
            value: '1'
        },
        {
            label: t('jbx.users.statusForbidden'),
            value: '0'
        }
    ],
    //用户类型
    "users_type": [
        {
            label: t('jbx.users.userTypeEmployee'),
            value: 'EMPLOYEE'
        },
        {
            label: t('jbx.users.userTypeSupplier'),
            value: 'SUPPLIER'
        },
        {
            label: t('jbx.users.userTypeCustomer'),
            value: 'CUSTOMER'
        },
        {
            label: t('jbx.users.userTypeContractor'),
            value: 'CONTRACTOR'
        },
        {
            label: t('jbx.users.userTypeDealer'),
            value: 'DEALER'
        },
        {
            label: t('jbx.users.userTypePartner'),
            value: 'PARTNER'
        },
        {
            label: t('jbx.users.userTypeExternal'),
            value: 'EXTERNAL'
        },
        {
            label: t('jbx.users.userTypeIntern'),
            value: 'INTERN'
        },
        {
            label: t('jbx.users.userTypeTemp'),
            value: 'TEMP'
        }
    ],
    //用户状态
    "users_state": [
        {
            label: t('jbx.users.userstateResident'),
            value: 'RESIDENT'
        },
        {
            label: t('jbx.users.userstateWithdrawn'),
            value: 'WITHDRAWN'
        },
        {
            label: t('jbx.users.userstateInactive'),
            value: 'INACTIVE'
        },
        {
            label: t('jbx.users.userstateRetiree'),
            value: 'RETIREE'
        }
    ],
    //状态
    "status": [
        {
            label: t('jbx.users.statusActive'),
            value: 1
        },
        {
            label: t('jbx.users.statusInactive'),
            value: 2
        },
        {
            label: t('jbx.users.statusForbidden'),
            value: 4
        },
        {
            label: t('jbx.users.statusLock'),
            value: 5
        },
        {
            label: t('jbx.users.statusDelete'),
            value: 9
        }
    ],
    //婚姻状态
    "users_married": [
        {
            label: t('jbx.users.marriedUnknown'),
            value: 0
        },
        {
            label: t('jbx.users.marriedSingle'),
            value: 1
        },
        {
            label: t('jbx.users.marriedMarried'),
            value: 2
        },
        {
            label: t('jbx.users.marriedDivorce'),
            value: 3
        },
        {
            label: t('jbx.users.marriedWidowed'),
            value: 4
        },
    ],
    "signature_types": [
        {
            label: 'NONE',
            value: 'NONE'
        },
        {
            label: 'RS256',
            value: 'RS256'
        },
        {
            label: 'RS384',
            value: 'RS384'
        },
        {
            label: 'RS512',
            value: 'RS512'
        },
        {
            label: 'HS256',
            value: 'HS256'
        },
        {
            label: 'HS384',
            value: 'HS384'
        },
        {
            label: 'HS512',
            value: 'HS512'
        }
    ],
    "user_api_types": [
        {
            label: 'NORMAL',
            value: 'NORMAL'
        },
        {
            label: 'SIGNING',
            value: 'SIGNING'
        },
        {
            label: 'ENCRYPTION',
            value: 'ENCRYPTION'
        },
        {
            label: 'SIGNING_ENCRYPTION',
            value: 'SIGNING_ENCRYPTION'
        },
    ],
    "encryption_algorithm": [
        {
            label: 'NONE',
            value: 'NONE'
        },
        {
            label: 'RSA1_5',
            value: 'RSA1_5'
        },
        {
            label: 'RSA_OAEP',
            value: 'RSA_OAEP'
        },
        {
            label: 'RSA-OAEP-256',
            value: 'RSA-OAEP-256'
        },
        {
            label: 'A128KW',
            value: 'A128KW'
        },
        {
            label: 'A192KW',
            value: 'A192KW'
        },
        {
            label: 'A256KW',
            value: 'A256KW'
        },
        {
            label: 'A128GCMKW',
            value: 'A128GCMKW'
        },
        {
            label: 'A192GCMKW',
            value: 'A192GCMKW'
        },
        {
            label: 'A256GCMKW',
            value: 'A256GCMKW'
        },
    ],
    "encryption_method": [
        {
            label: 'A128GCM',
            value: 'A128GCM'
        },
        {
            label: 'A192GCM',
            value: 'A192GCM'
        },
        {
            label: 'A256GCM',
            value: 'A256GCM'
        },
        {
            label: 'A128CBC-HS256',
            value: 'A128CBC-HS256'
        },
        {
            label: 'A192CBC-HS384',
            value: 'A192CBC-HS384'
        },
        {
            label: 'A256CBC-HS512',
            value: 'A256CBC-HS512'
        },
        {
            label: 'XC20P',
            value: 'XC20P'
        },
    ],
    "binding_types": [
        {
            label: 'Redirect-Post',
            value: 'Redirect-Post'
        },
        {
            label: 'Post-Post',
            value: 'Post-Post',
        },
        {
            label: 'IdpInit-Post',
            value: 'IdpInit-Post',
        },
        {
            label: 'Redirect-PostSimpleSign',
            value: 'Redirect-PostSimpleSign',
        },
        {
            label: 'Post-PostSimpleSign',
            value: 'Post-PostSimpleSign',
        },
        {
            label: 'IdpInit-PostSimpleSign',
            value: 'IdpInit-PostSimpleSign',
        }
    ],
    "encryption_algorithm_SAML": [
        {
            label: 'RSAwithSHA1',
            value: 'RSAwithSHA1',
        },
        {
            label: 'RSAwithSHA256',
            value: 'RSAwithSHA256',
        },
        {
            label: 'RSAwithSHA384',
            value: 'RSAwithSHA384',
        },
        {
            label: 'RSAwithSHA512',
            value: 'RSAwithSHA512',
        },
        {
            label: 'RSAwithMD5',
            value: 'RSAwithMD5',
        },
        {
            label: 'RSAwithRIPEMD160',
            value: 'RSAwithRIPEMD160',
        },
        {
            label: 'DSAwithSHA1',
            value: 'DSAwithSHA1',
        },
        {
            label: 'ECDSAwithSHA1',
            value: 'ECDSAwithSHA1',
        },
        {
            label: 'ECDSAwithSHA256',
            value: 'ECDSAwithSHA256',
        },
        {
            label: 'ECDSAwithSHA384',
            value: 'ECDSAwithSHA384',
        },
        {
            label: 'ECDSAwithSHA512',
            value: 'ECDSAwithSHA512',
        },
        {
            label: 'HMAC-MD5',
            value: 'HMAC-MD5',
        },
        {
            label: 'HMAC-SHA1',
            value: 'HMAC-SHA1',
        },
    ],
    "apps_abstracting": [
        {
            label: 'MD5',
            value: 'MD5',
        },
        {
            label: 'SHA1',
            value: 'SHA1',
        },
        {
            label: 'SHA256',
            value: 'SHA256',
        },
        {
            label: 'SHA384',
            value: 'SHA384',
        },
        {
            label: 'SHA512',
            value: 'SHA512',
        },
        {
            label: 'RIPEMD-160',
            value: 'RIPEMD-160',
        },
    ],
    "app_encryption_status": [
        {
            label: t('jbx.apps.samlEncryptedNo'),
            value: 'no',
        },
        {
            label: t('jbx.apps.samlEncrypted'),
            value: 'yes',
        },
    ],
    "app_nameid_format": [
        {
            label: 'persistent',
            value: 'persistent',
        },
        {
            label: 'transient',
            value: 'transient',
        },
        {
            label: 'emailAddress',
            value: 'emailAddress',
        },
        {
            label: 'X509SubjectName',
            value: 'X509SubjectName',
        },
        {
            label: 'WindowsDomainQualifiedName',
            value: 'WindowsDomainQualifiedName',
        },
        {
            label: 'unspecified',
            value: 'unspecified',
        },
        {
            label: 'entity',
            value: 'entity',
        }
    ],
    "apps_nameid_convert": [
        {
            label: t('jbx.apps.samlNameidconvertOriginal'),
            value: '1',
        },
        {
            label: t('jbx.apps.samlNameidconvertUpperCase'),
            value: '2',
        },
        {
            label: t('jbx.apps.samlNameidconvertLowerCase'),
            value: '3',
        }
    ],
    "apps_cert_type": [
        {
            label: t('jbx.ldapcontext.trustStore'),
            value: 'certificate',
        },
        {
            label: t('jbx.apps.samlFiletypeMetadataFile'),
            value: 'metadata_file',
        },
        {
            label: t('jbx.apps.samlFiletypeMetadataUrl'),
            value: 'metadata_url',
        }
    ],
    "apps_ret_account": [
        {
            label: t('jbx.users.username'),
            value: 'username',
        },
        {
            label: t('jbx.users.employeeNumber'),
            value: 'employeeNumber',
        },
        {
            label: t('jbx.menu.security.emailsenders'),
            value: 'email',
        },
        {
            label: t('jbx.users.mobile'),
            value: 'mobile',
        },
        {
            label: t('jbx.users.windowsAccount'),
            value: 'windowsaccount',
        },
        {
            label: t('jbx.users.id'),
            value: 'userId',
        }
    ],
    "apps_jwt_type": [
        {
            label: 'GET',
            value: '1',
        },
        {
            label: 'POST',
            value: '2',
        },
        {
            label: 'LTPA',
            value: '3',
        }
    ],
    "apps_token_encryption": [
        {
            label: 'DES',
            value: 'DES',
        },
        {
            label: 'DESede',
            value: 'DESede',
        },
        {
            label: 'Blowfish',
            value: 'Blowfish',
        },
        {
            label: 'AES',
            value: 'AES',
        }
    ],
    "apps_token_content": [
        {
            label: t('jbx.users.id'),
            value: 'userId',
        },
        {
            label: t('jbx.users.username'),
            value: 'username',
        },
        {
            label: t('jbx.users.displayName'),
            value: 'displayName',
        },
        {
            label: t('jbx.users.gender'),
            value: 'gender',
        },
        {
            label: t('jbx.users.idtype'),
            value: 'idtype',
        },
        {
            label: t('jbx.users.idCardNo'),
            value: 'idCardNo',
        },
        {
            label: t('jbx.users.mobile'),
            value: 'mobile',
        },
        {
            label: t('jbx.menu.security.emailsenders'),
            value: 'email',
        },
        {
            label: t('jbx.users.userType'),
            value: 'userType',
        },
        {
            label: t('jbx.users.employeeNumber'),
            value: 'employeeNumber',
        },
        {
            label: t('jbx.users.jobTitle'),
            value: 'jobTitle',
        },
        {
            label: t('jbx.users.departmentId'),
            value: 'departmentId',
        },
        {
            label: t('jbx.users.department'),
            value: 'department',
        },
        {
            label: '域账户',
            value: 'windowsAccount',
        },
    ],
    "apps_form_type": [
        {
            label: t('jbx.apps.credentialUserdefined'),
            value: '1',
        },
        {
            label: t('jbx.apps.credentialShared'),
            value: '2',
        },
        {
            label: t('jbx.apps.credentialSystem'),
            value: '3',
        },
    ],
    "apps_form_algorithm": [
        {
            label: 'NONE',
            value: 'NONE',
        },
        {
            label: 'MD5',
            value: 'MD5',
        },
        {
            label: 'SHA',
            value: 'SHA',
        },
        {
            label: 'SHA-1',
            value: 'SHA-1',
        },
        {
            label: 'SHA-256',
            value: 'SHA-256',
        },
        {
            label: 'SHA-384',
            value: 'SHA-384',
        },
        {
            label: 'SHA-512',
            value: 'SHA-512',
        },
        {
            label: 'MD5-HEX',
            value: 'MD5-HEX',
        },
        {
            label: 'SHA-HEX',
            value: 'SHA-HEX',
        },
        {
            label: 'SHA-256-HEX',
            value: 'SHA-256-HEX',
        },
        {
            label: 'SHA-384-HEX',
            value: 'SHA-384-HEX',
        },
        {
            label: 'SHA-512-HEX',
            value: 'SHA-512-HEX',
        }
    ],
    //访问控制类型
    "category": [
        {
            label: t('jbx.roles.category.dynamic'),
            value: 'dynamic',
        },
        {
            label: t('jbx.roles.category.static'),
            value: 'static',
        },
        {
            label: t('jbx.roles.category.app'),
            value: 'app',
        },
    ],
    //成员类型
    "role_members_type": [
        {
            label: t('jbx.roles.type.user'),
            value: 'USER',
        },
        {
            label: t('jbx.roles.type.userDynamic'),
            value: 'USER-DYNAMIC',
        },
        {
            label: t('jbx.roles.type.post'),
            value: 'POST',
        }
    ],
    //成员类型
    "user_gender_type": [
        {
            label: t('jbx.users.genderMale'),
            value: '2',
        },
        {
            label: t('jbx.users.genderFemale'),
            value: '1',
        },
        {
            label: t('jbx.resources.resourceType.oTHER'),
            value: '0',
        }
    ],
    //成员类型
    "sys_visible": [
        {
            label: t('jbx.text.display'),
            value: '1',
        },
        {
            label: t('jbx.apps.visibleHidden'),
            value: '0',
        }
    ],
    //成员类型
    "resource_type": [
        {
            label: t('jbx.resources.resourceType.mENU'),
            value: 'MENU',
        },
        {
            label: t('jbx.resources.resourceType.pAGE'),
            value: 'PAGE',
        },
        {
            label: 'API',
            value: 'API',
        },
        {
            label: 'SYSTEM',
            value: 'SYSTEM',
        },
        {
            label: t('jbx.resources.resourceType.mODULE'),
            value: 'MODULE',
        },
        {
            label: t('jbx.resources.resourceType.eLEMENT'),
            value: 'ELEMENT',
        },
        {
            label: t('jbx.resources.resourceType.bUTTON'),
            value: 'BUTTON',
        },
        {
            label: t('jbx.resources.resourceType.fILE'),
            value: 'FILE',
        },
        {
            label: t('jbx.resources.resourceType.dATA'),
            value: 'DATA',
        },
        {
            label: t('jbx.resources.resourceType.oTHER'),
            value: 'OTHER',
        }
    ],
    //请求方式
    "method_type": [
        {
            label: 'GET',
            value: 'GET',
        },
        {
            label: 'POST',
            value: 'POST',
        },
        {
            label: 'PUT',
            value: 'PUT',
        },
        {
            label: 'DELETE',
            value: 'DELETE',
        },
        {
            label: 'PATCH ',
            value: 'PATCH ',
        },
        {
            label: 'OPTIONS ',
            value: 'OPTIONS ',
        },
        {
            label: 'HEAD ',
            value: 'HEAD ',
        },
        {
            label: 'TRACE ',
            value: 'TRACE ',
        },
    ],
    //成员类型
    "ldapcontext_cp": [
        {
            label: 'ActiveDirectory',
            value: 'ActiveDirectory',
        },
        {
            label: 'OpenLDAP',
            value: 'OpenLDAP',
        },
        {
            label: 'StandardLDAP',
            value: 'StandardLDAP',
        },
    ],
    //成员类型
    "smsprovider": [
        {
            label: t('jbx.smsprovider.name.aliyun'),
            value: 'aliyun',
        },
        {
            label: t('jbx.smsprovider.name.tencentcloud'),
            value: 'tencentcloud',
        },
        {
            label: t('jbx.smsprovider.name.neteasesms'),
            value: 'neteasesms',
        },
        {
            label: t('jbx.smsprovider.name.email'),
            value: 'email',
        },
        {
            label: t('jbx.smsprovider.name.mq'),
            value: 'mq',
        },
    ],
    //证件类型
    "users_idType": [
        {
            label: t('jbx.users.idtypeUnknown'),
            value: 0
        },
        {
            label: t('jbx.users.idtypeIdcard'),
            value: 1
        },
        {
            label: t('jbx.users.idtypePassport'),
            value: 2
        },
        {
            label: t('jbx.users.idtypeStudentcard'),
            value: 3
        },
        {
            label: t('jbx.users.idtypeMilitarycard'),
            value: 4
        }
    ],
    "post_options": [
        {
            label: t('jbx.text.owns'),
            value: 'owns'
        },
        {
            label: t('jbx.text.assignable'),
            value: 'assignable'
        }
    ],
    "app_select_type": [
        {
            label: t('jbx.apps.id'),
            value: 'APPID'
        },
        {
            label: t('jbx.apps.name'),
            value: 'APPNAME'
        }
    ],
    //成员类型
    "system_app_type": [
        {
            label: "认证系统",
            value: 'sign',
        },
        {
            label: "管理系统",
            value: 'mgt',
        },
        {
            label: "OpenApi接口",
            value: 'openapi',
        },
        {
            label: "默认值",
            value: 'none',
        },
    ],
    //用户组类型
    "group_category_options": [
        {
            label: t('groupCategoryGeneral'),
            value: 'general',
        },
        {
            label: t('groupCategorySupervisor'),
            value: 'supervisor',
        },
        {
            label: t('groupCategoryAdministrator'),
            value: 'administrator',
        },
        {
            label: t('groupCategoryManager'),
            value: 'manager',
        },
    ],
    //科目类型
    "subjects_category": [
        {
            label: t('subjectAll'),
            value: "",
        },
        {
            label: t('subjectAssets'),
            value: 1,
        },
        {
            label: t('subjectLiabilities'),
            value: 2,
        },
        {
            label: t('subjectCommonAccounts'),

            value: 3,
        },
        {
            label: t('subjectEquity'),
            value: 4,
        },
        {
            label: t('subjectCosts'),

            value: 5,
        },
        {
            label: t('subjectProfitAndLoss'),
            value: 6,
        }
    ],
    //辅助核算
    "subjects_auxiliary": [
        {
            label: t('subjectProject'),
            value: "1",
        },
        {
            label: t('subjectCustomer'),
            value: "2",
        },
        {
            label: t('subjectProvider'),
            value: "3",
        },
        {
            label: t('subjectDept'),
            value: "4",
        },
        {
            label: t('subjectStaff'),
            value: "5",
        },
        {
            label: t('subjectStock'),
            value: "6",
        }
    ],
    //增值税种类
    "books_vat_type": [
        {
            label: "小规模纳税人",
            value: 0,
        },
        {
            label: "一般纳税人",
            value: 1,
        },
    ],
    //所属行业
    "books_industry": [
        {
            label: "农、林、牧、渔业",
            options: [
                {
                    value: 1,
                    label: "农业"
                },
                {
                    value: 2,
                    label: "林业"
                },
                {
                    value: 3,
                    label: "畜牧业"
                },
                {
                    value: 4,
                    label: "渔业"
                },
                {
                    value: 5,
                    label: "农、林、牧、渔专业及辅助性活动"
                }
            ]
        },
        {
            label: "采矿业",
            options: [
                {
                    value: 6,
                    label: "煤炭开采和洗选业"
                },
                {
                    value: 7,
                    label: "石油和天然气开采业"
                },
                {
                    value: 8,
                    label: "黑色金属矿采选业"
                },
                {
                    value: 9,
                    label: "有色金属矿采选业"
                },
                {
                    value: 10,
                    label: "非金属矿采选业"
                },
                {
                    value: 11,
                    label: "开采专业及辅助性活动"
                },
                {
                    value: 12,
                    label: "其他采矿业"
                }
            ]
        },
        {
            label: "制造业",
            options: [
                {
                    value: 13,
                    label: "农副食品加工业"
                },
                {
                    value: 14,
                    label: "食品制造业"
                },
                {
                    value: 15,
                    label: "酒、饮料和精制茶制造业"
                },
                {
                    value: 16,
                    label: "烟草制品业"
                },
                {
                    value: 17,
                    label: "纺织业"
                },
                {
                    value: 18,
                    label: "纺织服装、服饰业"
                },
                {
                    value: 19,
                    label: "皮革、毛皮、羽毛及其制品和制鞋业"
                },
                {
                    value: 20,
                    label: "木材加工和木、竹、藤、棕、草制品业"
                },
                {
                    value: 21,
                    label: "家具制造业"
                },
                {
                    value: 22,
                    label: "造纸和纸制品业"
                },
                {
                    value: 23,
                    label: "印刷和记录媒介复制业"
                },
                {
                    value: 24,
                    label: "文教、工美、体育和娱乐用品制造业"
                },
                {
                    value: 25,
                    label: "石油、煤炭及其他燃料加工业"
                },
                {
                    value: 26,
                    label: "化学原料和化学制品制造业"
                },
                {
                    value: 27,
                    label: "医药制造业"
                },
                {
                    value: 28,
                    label: "化学纤维制造业"
                },
                {
                    value: 29,
                    label: "橡胶和塑料制品业"
                },
                {
                    value: 30,
                    label: "非金属矿物制品业"
                },
                {
                    value: 31,
                    label: "黑色金属冶炼和压延加工业"
                },
                {
                    value: 32,
                    label: "有色金属冶炼和压延加工业"
                },
                {
                    value: 33,
                    label: "金属制品业"
                },
                {
                    value: 34,
                    label: "通用设备制造业"
                },
                {
                    value: 35,
                    label: "专用设备制造业"
                },
                {
                    value: 36,
                    label: "汽车制造业"
                },
                {
                    value: 37,
                    label: "铁路、船舶、航空航天和其他运输设备制造业"
                },
                {
                    value: 38,
                    label: "电气机械和器材制造业"
                },
                {
                    value: 39,
                    label: "计算机、通信和其他电子设备制造业"
                },
                {
                    value: 40,
                    label: "仪器仪表制造业"
                },
                {
                    value: 41,
                    label: "其他制造业"
                },
                {
                    value: 42,
                    label: "废弃资源综合利用业"
                },
                {
                    value: 43,
                    label: "金属制品、机械和设备修理业"
                }
            ]
        },
        {
            label: "电力、热力、燃气及水生产和供应业",
            options: [
                {
                    value: 44,
                    label: "电力、热力生产和供应业"
                },
                {
                    value: 45,
                    label: "燃气生产和供应业"
                },
                {
                    value: 46,
                    label: "水的生产和供应业"
                }
            ]
        },
        {
            label: "建筑业",
            options: [
                {
                    value: 47,
                    label: "房屋建筑业"
                },
                {
                    value: 48,
                    label: "土木工程建筑业"
                },
                {
                    value: 49,
                    label: "建筑安装业"
                },
                {
                    value: 50,
                    label: "建筑装饰、装修和其他建筑业"
                }
            ]
        },
        {
            label: "批发和零售业",
            options: [
                {
                    value: 51,
                    label: "批发业"
                },
                {
                    value: 52,
                    label: "零售业"
                }
            ]
        },
        {
            label: "交通运输、仓储和邮政业",
            options: [
                {
                    value: 53,
                    label: "铁路运输业"
                },
                {
                    value: 54,
                    label: "道路运输业"
                },
                {
                    value: 55,
                    label: "水上运输业"
                },
                {
                    value: 56,
                    label: "航空运输业"
                },
                {
                    value: 57,
                    label: "管道运输业"
                },
                {
                    value: 58,
                    label: "多式联运和运输代理业"
                },
                {
                    value: 59,
                    label: "装卸搬运和仓储业"
                },
                {
                    value: 60,
                    label: "邮政业"
                }
            ]
        },
        {
            label: "住宿和餐饮业",
            options: [
                {
                    value: 61,
                    label: "住宿业"
                },
                {
                    value: 62,
                    label: "餐饮业"
                }
            ]
        },
        {
            label: "信息传输、软件和信息技术服务业",
            options: [
                {
                    value: 63,
                    label: "电信、广播电视和卫星传输服务"
                },
                {
                    value: 64,
                    label: "互联网和相关服务"
                },
                {
                    value: 65,
                    label: "软件和信息技术服务业"
                }
            ]
        },
        {
            label: "金融业",
            options: [
                {
                    value: 66,
                    label: "货币金融服务"
                },
                {
                    value: 67,
                    label: "资本市场服务"
                },
                {
                    value: 68,
                    label: "保险业"
                },
                {
                    value: 69,
                    label: "其他金融业"
                }
            ]
        },
        {
            label: "房地产业",
            options: [
                {
                    value: 70,
                    label: "房地产业"
                }
            ]
        },
        {
            label: "租赁和商务服务业",
            options: [
                {
                    value: 71,
                    label: "租赁业"
                },
                {
                    value: 72,
                    label: "商务服务业"
                }
            ]
        },
        {
            label: "科学研究和技术服务业",
            options: [
                {
                    value: 73,
                    label: "研究和试验发展"
                },
                {
                    value: 74,
                    label: "专业技术服务业"
                },
                {
                    value: 75,
                    label: "科技推广和应用服务业"
                }
            ]
        },
        {
            label: "水利、环境和公共设施管理业",
            options: [
                {
                    value: 76,
                    label: "水利管理业"
                },
                {
                    value: 77,
                    label: "生态保护和环境治理业"
                },
                {
                    value: 78,
                    label: "公共设施管理业"
                },
                {
                    value: 79,
                    label: "土地管理业"
                }
            ]
        },
        {
            label: "居民服务、修理和其他服务业",
            options: [
                {
                    value: 80,
                    label: "居民服务业"
                },
                {
                    value: 81,
                    label: "机动车、电子产品和日用产品修理业"
                },
                {
                    value: 82,
                    label: "其他服务业"
                }
            ]
        },
        {
            label: "教育",
            options: [
                {
                    value: 83,
                    label: "教育"
                }
            ]
        },
        {
            label: "卫生和社会工作",
            options: [
                {
                    value: 84,
                    label: "卫生"
                },
                {
                    value: 85,
                    label: "社会工作"
                }
            ]
        },
        {
            label: "文化、体育和娱乐业",
            options: [
                {
                    value: 86,
                    label: "新闻和出版业"
                },
                {
                    value: 87,
                    label: "广播、电视、电影和录音制作业"
                },
                {
                    value: 88,
                    label: "文化艺术业"
                },
                {
                    value: 89,
                    label: "体育"
                },
                {
                    value: 90,
                    label: "娱乐业"
                }
            ]
        },
        {
            label: "公共管理、社会保障和社会组织",
            options: [
                {
                    value: 91,
                    label: "中国共产党机关"
                },
                {
                    value: 92,
                    label: "国家机构"
                },
                {
                    value: 93,
                    label: "人民政协、民主党派"
                },
                {
                    value: 94,
                    label: "社会保障"
                },
                {
                    value: 95,
                    label: "群众团体、社会团体和其他成员组织"
                },
                {
                    value: 96,
                    label: "基层群众自治组织及其他组织"
                }
            ]
        },
        {
            label: "国际组织",
            options: [
                {
                    value: 97,
                    label: "国际组织"
                }
            ]
        },
        {
            label: "其他行业",
            options: [
                {
                    value: 98,
                    label: "其他行业"
                }
            ]
        }
    ],
    //工资项目
    "salaryTypes": [
        {id: 'basic', name: '基本工资', type: 'base'},
        {id: 'post', name: '岗位工资', type: 'base'},
        {id: 'performance', name: '绩效', type: 'base'},
        {id: 'bonus', name: '奖金', type: 'extra'},
        {id: 'overtime', name: '加班补贴', type: 'extra'},
        {id: 'allowance', name: '津贴', type: 'extra'},
        {id: 'backPay', name: '补发工资', type: 'extra'},
        {id: 'insurance', name: '社保扣除', type: 'deduction'},
        {id: 'providentFund', name: '公积金扣除', type: 'deduction'},
        {id: 'attendance', name: '请假考勤', type: 'deduction'},
        {id: 'tax', name: '个税扣除', type: 'deduction'},
        {id: 'otherDeductions', name: '其他扣额', type: 'deduction'},
    ],
    // 运算符
    "operators": [
        {id: '+', name: '+'},
        {id: '-', name: '-'},
        {id: '*', name: '*'},
        {id: '/', name: '/'}
    ],
    //员工类型
    "employee_types": [
        {
            label: "普通员工",
            value: 'NORMAL'
        },
        {
            label: '兼职',
            value: 'PARTTIME'
        },
        {
            label: '实习生',
            value: 'INTERN'
        },
        {
            label: '退休返聘',
            value: 'RETIREMENT'
        }
    ],
    "voucherTypes": [
        {
            label: "计提工资",
            value: 0
        },
        {
            label: '发放工资',
            value: 1
        },
        {
            label: '收票（兼职）',
            value: 2
        },
        {
            label: '发放（兼职）',
            value: 3
        }
    ],
    "wordHeads": [
        {
            label: "记",
            value: "记"
        },
        {
            label: "收",
            value: "收"
        },
        {
            label: "付",
            value: "付"
        },
        {
            label: "转",
            value: "转"
        }
    ],
    "account_balance_type": [
        {
            label: "余额",
            value: "BALANCE"
        },
        {
            label: "借方余额",
            value: "DEBIT_BALANCE"
        },
        {
            label: "贷方余额",
            value: "CREDIT_BALANCE"
        },
        {
            label: "科目借方余额",
            value: "SUBJECT_DEBIT_BALANCE"
        },
        {
            label: "科目贷方余额",
            value: "SUBJECT_CREDIT_BALANCE"
        },
        {
            label: "本级科目借方余额",
            value: "CURRENT_SUBJECT_DEBIT_BALANCE"
        },
        {
            label: "本级科目贷方余额",
            value: "CURRENT_SUBJECT_CREDIT_BALANCE"
        }
    ],
    "account_income_balance_type": [
        {
            label: "损益发生额",
            value: "PROFIT_AND_LOSS_AMOUNT"
        },
        {
            label: "借方发生额",
            value: "DEBIT_AMOUNT"
        },
        {
            label: "贷方发生额",
            value: "CREDIT_AMOUNT"
        }
    ],
    "statistics_period": [
        {
            label: "本期",
            value: "currentPeriod"
        },
        {
            label: "上期",
            value: "lastPeriod"
        },
        {
            label: "本年",
            value: "currentYear"
        },
        {
            label: "上年",
            value: "lastYear"
        },
    ],
    "salary_values": [
        {
            label: "公司成本",
            value: "companyCosts"
        },
        {
            label: "应发工资",
            value: "salaryPayable"
        },
        {
            label: "实发工资",
            value: "actualSalary"
        },
        {
            label: "个人所得税",
            value: "personalIncomeTax"
        },
        {
            label: "个人代扣社保",
            value: "personalWithholdingOfSocialSecurity"
        },
        {
            label: "个人代扣公积金",
            value: "personalWithholdingOfProvidentFund"
        },
        {
            label: "企业缴纳社保",
            value: "enterprisesPaySocialInsurance"
        },
        {
            label: "企业缴纳公积金",
            value: "providentFundPaidByEnterprises"
        }
    ],
    "labor_fee_values": [
        {
            label: "劳务费总额",
            value: "companyCosts"
        },
        {
            label: "实发劳务费",
            value: "actualSalary"
        },
        {
            label: "代扣个人所得税",
            value: "personalIncomeTax"
        }
    ],
    "salary_directions": [
        {
            label: "借",
            value: "1"
        },
        {
            label: "贷",
            value: "2"
        }
    ],
    "period_type": [
        {
            label: "月度",
            value: "month"
        },
        {
            label: "季度",
            value: "quarter"
        },
        {
            label: "年度",
            value: "year"
        }
    ],
    "depreciation_method": [
        {
            label: "平均年限法",
            value: 1
        },
        {
            label: "双倍余额递减法",
            value: 2
        },
        {
            label: "不提折旧",
            value: 3
        }
    ],
    "assets_status": [
        {
            label: "使用中",
            value: "active"
        },
        {
            label: "处置中",
            value: "disposal"
        },
        {
            label: "已处置",
            value: "disposed"
        }
    ],
    "expense_type": [
        {
            label: "差旅",
            value: "travel"
        },
        {
            label: "办公",
            value: "office"
        },
        {
            label: "采购",
            value: "purchase"
        },
        {
            label: "招待",
            value: "entertainment"
        },
        {
            label: "交通",
            value: "transport"
        },
        {
            label: "其他",
            value: "other"
        }
    ],
    "payment_method": [
        {
            label: "现金",
            value: "cash"
        },
        {
            label: "银行卡",
            value: "bankCard"
        },
        {
            label: "微信",
            value: "wechat"
        },
        {
            label: "支付宝",
            value: "alipay"
        },
        {
            label: "其他",
            value: "other"
        }
    ],
    "inst_types": [
        {
            label: "企业",
            value: 0
        },
        {
            label: "个人",
            value: 1
        }
    ],
    "invoice_types": [
        {
            label: "增值税专用发票",
            value: 'VAT_SPECIAL'
        },
        {
            label: "增值税普通发票",
            value: 'VAT_NORMAL'
        },
        {
            label: "电子发票",
            value: 'VAT_ELECTRONIC'
        },
        {
            label: "机动车销售发票",
            value: 'VEHICLE'
        },
        {
            label: "通行费发票",
            value: 'TOLL'
        },
        {
            label: "出口发票",
            value: 'EXPORT'
        },
        {
            label: "其他票据",
            value: 'OTHER'
        },
    ],
    "invoice_status": [
        {
            label: "新建",
            value: 'NEW'
        },
        {
            label: "已开具",
            value: 'ISSUED'
        },
        {
            label: "已接收",
            value: 'RECEIVED'
        },
        {
            label: "已支付",
            value: 'PAID'
        },
        {
            label: "已收款",
            value: 'COLLECTED'
        },
        {
            label: "已归档",
            value: 'ARCHIVED'
        },
        {
            label: "作废",
            value: 'CANCELED'
        },
        {
            label: "红冲",
            value: 'RED_INVOICE'
        },
        {
            label: "已换开",
            value: 'REPLACED'
        }
    ],
    "selected_values": [
        {
            label: "金额合计（不含税）",
            value: 'TOTAL_AMOUNT'
        },
        {
            label: "税额",
            value: 'TAX_AMOUNT'
        },
        {
            label: "价税合计",
            value: 'TOTAL_WITH_TAX'
        }
    ],
    "client_type": [
        {
            label: "内部员工",
            value: 1
        },
        {
            label: "外部合作方",
            value: 2
        },
        {
            label: "系统对接",
            value: 3
        },
        {
            label: "其他",
            value: 4
        }
    ],
    "resources_type": [
        {
            label: t('jbx.resources.resourceType.mENU'),
            value: 'menu',
        },
        {
            label: 'Api',
            value: 'api',
        },
        {
            label: '按钮',
            value: 'button',
        },
        {
            label: 'OpenApi',
            value: 'openApi',
        },
        {
            label: 'API代理',
            value: 'proxy',
        },
    ],
    "proxy_auth_type": [
        {
            label: 'Base',
            value: 'BASE',
        },
        {
            label: 'JWT',
            value: 'JWT',
        },
        {
            label: 'OAuth2',
            value: 'OAUTH2',
        }
    ]
}

export default distData
