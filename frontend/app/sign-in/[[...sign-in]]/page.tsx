import { SignIn } from '@clerk/nextjs'
import { AuthLayout } from '@/components/auth-layout'

export default function SignInPage() {
  return (
    <AuthLayout>
      <SignIn 
        appearance={{
          elements: {
            socialButtonsBlockButton: {
              display: 'none'
            }
          }
        }}
      />
    </AuthLayout>
  )
}