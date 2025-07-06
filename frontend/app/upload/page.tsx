"use client"

import type React from "react"

import { useState, useRef } from "react"
import { useRouter } from "next/navigation"
import Image from "next/image"
import { Upload, Camera, FileText, ArrowLeft, CheckCircle } from "lucide-react"
import { useUser } from "@clerk/nextjs"
import { useReceiptService } from "@/services/receipt-service"

interface ExtractedData {
  retailer: string;
  purchaseDate: string;
  purchaseTime: string;
  total: number;
  items: Array<{ shortDescription: string; price: number }>
}

interface ProcessingResult {
  pointsEarned: number
  extractedData: ExtractedData
}

export default function UploadPage() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [extractedData, setExtractedData] = useState<ExtractedData | null>(null)
  const [result, setResult] = useState<ProcessingResult | null>(null)
  const [error, setError] = useState("")
  const [apiResponse, setApiResponse] = useState<any>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const { user } = useUser()
  const router = useRouter()
  const { uploadReceipt } = useReceiptService()

  const handleFileSelect = (file: File) => {
    if (!file.type.startsWith("image/")) {
      setError("Please select an image file")
      return
    }

    // Check file size (configurable via environment variable)
    const maxSizeBytes = process.env.NEXT_PUBLIC_MAX_FILE_SIZE_BYTES 
      ? parseInt(process.env.NEXT_PUBLIC_MAX_FILE_SIZE_BYTES) 
      : 5 * 1024 * 1024 // 5MB default
    const maxSizeMB = maxSizeBytes / (1024 * 1024)
    
    if (file.size > maxSizeBytes) {
      setError(`File size too large. Please select a file smaller than ${maxSizeMB}MB`)
      return
    }

    setSelectedFile(file)
    setPreviewUrl(URL.createObjectURL(file))
    setError("")
    setExtractedData(null)
    setResult(null)
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    const file = e.dataTransfer.files[0]
    if (file) handleFileSelect(file)
  }

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) handleFileSelect(file)
  }

  const extractData = async () => {
    if (!selectedFile) return

    setIsProcessing(true)
    setError("")

    try {
      const response = await uploadReceipt(selectedFile)
      setApiResponse(response)
      
      const extractedData: ExtractedData = {
        retailer: response.receipt.retailer,
        purchaseDate: response.receipt.purchaseDate,
        purchaseTime: response.receipt.purchaseTime,
        total: response.receipt.total,
        items: response.receipt.items,
      }

      setExtractedData(extractedData)
    } catch (err) {
      setError("Failed to extract data from receipt")
      console.error("Receipt upload error:", err)
    } finally {
      setIsProcessing(false)
    }
  }

  const submitReceipt = async () => {
    if (!extractedData || !apiResponse) return

    setIsProcessing(true)

    try {
      const pointsEarned = apiResponse.pointsEarned

      setResult({
        pointsEarned,
        extractedData,
      })
    } catch (err) {
      setError("Failed to submit receipt")
    } finally {
      setIsProcessing(false)
    }
  }

  const resetUpload = () => {
    setSelectedFile(null)
    setPreviewUrl(null)
    setExtractedData(null)
    setResult(null)
    setError("")
    setApiResponse(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ""
    }
  }

  if (result) {
    return (
      <div className="min-h-screen bg-gray-50 p-4">
        <div className="max-w-2xl mx-auto">
          <button
            onClick={() => router.push("/dashboard")}
            className="flex items-center text-gray-600 hover:text-gray-900 mb-6"
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Dashboard
          </button>

          <div className="bg-white rounded-lg shadow-md p-8">
            <div className="text-center mb-8">
              <div className="flex justify-center mb-4">
                <div className="p-3 bg-green-100 rounded-full">
                  <CheckCircle className="h-8 w-8 text-green-600" />
                </div>
              </div>
              <h1 className="text-2xl font-bold text-green-600 mb-2">Receipt Processed Successfully!</h1>
              <p className="text-gray-600">
                Your receipt has been processed and points have been added to your account
              </p>
            </div>

            <div className="text-center mb-8">
              <div className="text-4xl font-bold text-green-600 mb-2">+{result.pointsEarned} Points</div>
              <p className="text-gray-600">Added to your account</p>
            </div>

            <div className="border-t pt-6 mb-8">
              <h3 className="font-semibold mb-4">Receipt Summary</h3>
              <div className="grid grid-cols-2 gap-4 text-sm mb-4">
                <div>
                  <span className="text-gray-500">Merchant:</span>
                  <p className="font-medium">{result.extractedData.retailer}</p>
                </div>
                <div>
                  <span className="text-gray-500">Date:</span>
                  <p className="font-medium">{result.extractedData.purchaseDate}</p>
                </div>
                <div>
                  <span className="text-gray-500">Total:</span>
                  <p className="font-medium">${result.extractedData.total.toFixed(2)}</p>
                </div>
                <div>
                  <span className="text-gray-500">Items:</span>
                  <p className="font-medium">{result.extractedData.items.length} items</p>
                </div>
              </div>
            </div>

            <div className="flex gap-4">
              <button
                onClick={resetUpload}
                className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                Upload Another Receipt
              </button>
              <button
                onClick={() => router.push("/dashboard")}
                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                View Dashboard
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Upload Receipt</h1>
            <p className="text-gray-600">Upload your receipt to earn points</p>
          </div>
          <button
            onClick={() => router.push("/dashboard")}
            className="flex items-center px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Dashboard
          </button>
        </div>

        <div className="grid lg:grid-cols-2 gap-6">
          {/* Upload Section */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold mb-2">Upload Receipt</h2>
            <p className="text-gray-600 mb-6">Take a photo or upload an image of your receipt</p>

            {!selectedFile ? (
              <div
                className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-gray-400 transition-colors cursor-pointer"
                onDrop={handleDrop}
                onDragOver={(e) => e.preventDefault()}
                onClick={() => fileInputRef.current?.click()}
              >
                <div className="flex flex-col items-center space-y-4">
                  <div className="p-3 bg-blue-100 rounded-full">
                    <Upload className="h-8 w-8 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-lg font-medium">Drop your receipt here</p>
                    <p className="text-gray-500">or click to browse files</p>
                  </div>
                  <div className="flex gap-2">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      <Camera className="mr-1 h-3 w-3" />
                      Photo
                    </span>
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      <FileText className="mr-1 h-3 w-3" />
                      Digital
                    </span>
                  </div>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="relative">
                  <Image
                    src={previewUrl! || "/placeholder.svg"}
                    alt="Receipt preview"
                    width={400}
                    height={300}
                    className="w-full h-64 object-cover rounded-lg border"
                  />
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={extractData}
                    disabled={isProcessing || !!extractedData}
                    className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {isProcessing ? (
                      <div className="flex items-center justify-center">
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                        Extracting Data...
                      </div>
                    ) : extractedData ? (
                      "Data Extracted"
                    ) : (
                      "Extract Data"
                    )}
                  </button>
                  <button
                    onClick={resetUpload}
                    className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                  >
                    Remove
                  </button>
                </div>
              </div>
            )}

            <input ref={fileInputRef} type="file" accept="image/*" onChange={handleFileInput} className="hidden" />

            {error && (
              <div className="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md">{error}</div>
            )}
          </div>

          {/* Extracted Data Section */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold mb-2">Extracted Data</h2>
            <p className="text-gray-600 mb-6">Review the extracted information before submitting</p>

            {!extractedData ? (
              <div className="text-center py-8 text-gray-500">
                <FileText className="h-12 w-12 mx-auto mb-4 opacity-50" />
                <p>Upload and extract data from your receipt to see details here</p>
              </div>
            ) : (
              <div className="space-y-6">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium text-gray-500">Merchant</label>
                    <p className="font-medium">{extractedData.retailer}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Date</label>
                    <p className="font-medium">{extractedData.purchaseDate}</p>
                  </div>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-500">Total Amount</label>
                  <p className="text-2xl font-bold text-green-600">${extractedData.total.toFixed(2)}</p>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-500 mb-2 block">
                    Items ({extractedData.items.length})
                  </label>
                  <div className="space-y-2 max-h-40 overflow-y-auto">
                    {extractedData.items.map((item, index) => (
                      <div key={index} className="flex justify-between text-sm">
                        <span>{item.shortDescription}</span>
                        <span className="font-medium">${item.price.toFixed(2)}</span>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t">
                  <div className="flex justify-between items-center mb-4">
                    <span className="font-medium">Points to Earn:</span>
                    <span className="text-xl font-bold text-blue-600">+{Math.floor(extractedData.total)} points</span>
                  </div>
                  <button
                    onClick={submitReceipt}
                    disabled={isProcessing}
                    className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {isProcessing ? (
                      <div className="flex items-center justify-center">
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                        Submitting...
                      </div>
                    ) : (
                      "Submit Receipt"
                    )}
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
