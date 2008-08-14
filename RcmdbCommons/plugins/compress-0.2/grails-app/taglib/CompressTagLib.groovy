import com.planetj.servlet.filter.compression.CompressingFilterStats

class CompressTagLib {
    static namespace = "compress"

    def numRequestsCompressed = {
        out << getStats(servletContext)?.numRequestsCompressed
    }

    def numResponsesCompressed = {
        out << getStats(servletContext)?.numResponsesCompressed
    }

    def requestAverageCompressionRatio = {
        out << getStats(servletContext)?.requestAverageCompressionRatio
    }

    def requestCompressedBytes = {
        out << getStats(servletContext)?.requestCompressedBytes
    }

    def requestInputBytes = {
        out << getStats(servletContext)?.requestInputBytes
    }

    def responseAverageCompressionRatio = {
        out << getStats(servletContext)?.responseAverageCompressionRatio
    }

    def responseCompressedBytes = {
        out << getStats(servletContext)?.responseCompressedBytes
    }

    def responseInputBytes = {
        out << getStats(servletContext)?.responseInputBytes
    }

    def totalRequestsNotCompressed = {
        out << getStats(servletContext)?.totalRequestsNotCompressed
    }

    def totalResponsesNotCompressed = {
        out << getStats(servletContext)?.totalResponsesNotCompressed
    }

    def getStats(context) {
        context[CompressingFilterStats.STATS_KEY]
    }
}