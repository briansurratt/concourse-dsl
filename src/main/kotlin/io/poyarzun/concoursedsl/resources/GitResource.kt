package io.poyarzun.concoursedsl.resources

import io.poyarzun.concoursedsl.domain.Pipeline
import io.poyarzun.concoursedsl.domain.Resource
import io.poyarzun.concoursedsl.dsl.Init
import io.poyarzun.concoursedsl.dsl.baseResource

class GitSourceProps(
        val uri: String,
        var branch: String? = null,
        var privateKey: String? = null,
        var username: String? = null,
        var password: String? = null,
        var paths: MutableList<String>? = null,
        var ignorePaths: MutableList<String>? = null,
        var skipSslVerification: Boolean? = null,
        var tagFilter: String? = null,
        var gitConfig: MutableList<GitConfig>? = null,
        var disableCiSkip: Boolean? = null,
        var commitVerificationKeys: MutableList<String>? = null,
        var commitVerificationKeyIds: MutableList<String>? = null,
        var gpgKeyserver: String? = null,
        var gitCryptKey: String? = null,
        var httpsTunnel: HttpProxy? = null
) {
    class GitConfig(val name: String, val value: String)
    class HttpProxy(
            val proxyHost: String,
            val proxyPort: String,
            var proxyUser: String,
            var proxyPassword: String
    )
}

fun Pipeline.gitResource(name: String, uri: String, init: Init<Resource<GitSourceProps>>) =
        this.baseResource(name, "git", { GitSourceProps(uri) }, init)
