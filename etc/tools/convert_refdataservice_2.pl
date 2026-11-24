# This script is supposed to be run _after_ convert_refdataservice_1.pl (see first the documentation
# there).
# It takes care of places such as
#    RefDataService service = getRefDataService();
#    service.findOrderStatusBsEntity(...);
# I.e., where a call doesn't follow the pattern "getRefDataService().find...Entity".
#
# How to run it (for example):
#    find logon-business/src -name '*.java' | xargs perl -pi etc/tools/convert_refdataservice_1.pl
#
# marius, 2013-03-08

# Skip LangTx and already converted stuff
if (/LangTxEntity/ || /findBsEntity/ || /findTxEntit/ || /findActiveBsEntity/ ||
/findActiveTxEntity/) {
    next;
}
if (/findActive([A-Za-z]+)BsEntityList/) {
    s/\.findActive([A-Za-z]+)BsEntityList\(/.findActiveBsEntityList(${1}BsEntity.class/;
}
elsif (/find([A-Za-z]+)BsEntityList\(\)/) {
    s/\.find([A-Za-z]+)BsEntityList\(/.findBsEntityList(${1}BsEntity.class/;
}
else {
    s/\.find([A-Za-z]+)BsEntityList\(/.findBsEntityList(${1}BsEntity.class, /;
}
s/\.find([A-Za-z]+)BsEntity\(/.findBsEntity(${1}BsEntity.class, /;
s/\.find([A-Za-z]+)TxEntity\(/.findTxEntity(${1}TxEntity.class, /;
if (/find[A-Za-z]+TxEntityList\(\)/) {
    s/\.find([A-Za-z]+)TxEntityList\(/.findTxEntityList(${1}TxEntity.class/;
}
else {
    s/\.find([A-Za-z]+)TxEntityList\(/.findTxEntityList(${1}TxEntity.class, /;
}
s/\.find([A-Za-z]+)TxEntityFiltered\(/.findTxEntityFiltered(${1}TxEntity.class, /;
s/\.find([A-Za-z]+)BsEntityFiltered\(/.findBsEntityFiltered(${1}BsEntity.class, /;
