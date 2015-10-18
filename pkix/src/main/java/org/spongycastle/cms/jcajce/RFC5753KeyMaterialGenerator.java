package org.spongycastle.cms.jcajce;

import java.io.IOException;

import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Pack;

class RFC5753KeyMaterialGenerator
    implements KeyMaterialGenerator
{
    public byte[] generateKDFMaterial(ASN1ObjectIdentifier keyAlgorithm, int keySize, byte[] userKeyMaterialParameters)
    {
        ECCCMSSharedInfo eccInfo;

        if (CMSUtils.isDES(keyAlgorithm.getId()) || keyAlgorithm.equals(PKCSObjectIdentifiers.id_alg_CMSRC2wrap))
        {
            eccInfo = new ECCCMSSharedInfo(new AlgorithmIdentifier(keyAlgorithm, DERNull.INSTANCE), userKeyMaterialParameters, Pack.intToBigEndian(keySize));
        }
        else
        {
            eccInfo = new ECCCMSSharedInfo(new AlgorithmIdentifier(keyAlgorithm), userKeyMaterialParameters, Pack.intToBigEndian(keySize));
        }

        try
        {
            return eccInfo.getEncoded(ASN1Encoding.DER);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Unable to create KDF material: " + e);
        }
    }
}
