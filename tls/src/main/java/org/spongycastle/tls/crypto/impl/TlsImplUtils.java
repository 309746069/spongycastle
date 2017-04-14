package org.spongycastle.tls.crypto.impl;

import org.spongycastle.tls.ExporterLabel;
import org.spongycastle.tls.ProtocolVersion;
import org.spongycastle.tls.SecurityParameters;
import org.spongycastle.tls.crypto.TlsCryptoParameters;
import org.spongycastle.tls.crypto.TlsSecret;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Strings;

/**
 * Useful utility methods.
 */
public class TlsImplUtils
{
    public static boolean isSSL(TlsCryptoParameters cryptoParams)
    {
        return cryptoParams.getServerVersion().isSSL();
    }

    public static boolean isTLSv11(ProtocolVersion version)
    {
        return ProtocolVersion.TLSv11.isEqualOrEarlierVersionOf(version.getEquivalentTLSVersion());
    }

    public static boolean isTLSv11(TlsCryptoParameters cryptoParams)
    {
        return isTLSv11(cryptoParams.getServerVersion());
    }

    public static boolean isTLSv12(ProtocolVersion version)
    {
        return ProtocolVersion.TLSv12.isEqualOrEarlierVersionOf(version.getEquivalentTLSVersion());
    }

    public static boolean isTLSv12(TlsCryptoParameters cryptoParams)
    {
        return isTLSv12(cryptoParams.getServerVersion());
    }

    public static byte[] calculateKeyBlock(TlsCryptoParameters cryptoParams, int length)
    {
        SecurityParameters securityParameters = cryptoParams.getSecurityParameters();
        TlsSecret master_secret = securityParameters.getMasterSecret();
        byte[] seed = Arrays.concatenate(securityParameters.getServerRandom(), securityParameters.getClientRandom());

        if (isSSL(cryptoParams))
        {
            return master_secret.deriveSSLKeyBlock(seed, length).extract();
        }

        return PRF(cryptoParams, master_secret, ExporterLabel.key_expansion, seed, length).extract();
    }

    public static TlsSecret PRF(TlsCryptoParameters cryptoParams, TlsSecret secret, String asciiLabel, byte[] seed, int length)
    {
        ProtocolVersion version = cryptoParams.getServerVersion();

        if (version.isSSL())
        {
            throw new IllegalStateException("No PRF available for SSLv3 session");
        }

        byte[] label = Strings.toByteArray(asciiLabel);
        byte[] labelSeed = Arrays.concatenate(label, seed);

        int prfAlgorithm = cryptoParams.getSecurityParameters().getPrfAlgorithm();

        return secret.deriveUsingPRF(prfAlgorithm, labelSeed, length);
    }
}
